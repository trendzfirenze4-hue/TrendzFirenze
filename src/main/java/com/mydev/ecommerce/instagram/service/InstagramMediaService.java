

package com.mydev.ecommerce.instagram.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydev.ecommerce.instagram.dto.InstagramMediaItemDto;
import com.mydev.ecommerce.instagram.dto.InstagramPostDto;
import com.mydev.ecommerce.instagram.entity.InstagramAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class InstagramMediaService {

    private final InstagramTokenService instagramTokenService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${instagram.graph-version:v25.0}")
    private String graphVersion;

    @Value("${instagram.app-secret}")
    private String appSecret;

    public InstagramMediaService(
            InstagramTokenService instagramTokenService,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.instagramTokenService = instagramTokenService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<InstagramPostDto> fetchLatestMediaPosts(int limit) {
        InstagramAuth auth = instagramTokenService.getActiveAuthOrThrow();

        if (auth.isExpired()) {
            throw new RuntimeException("Instagram token expired. Manual reconnect required.");
        }

        String appSecretProof = createAppSecretProof(auth.getAccessToken(), appSecret);

        String url = UriComponentsBuilder
                .fromHttpUrl("https://graph.facebook.com/" + graphVersion + "/" + auth.getInstagramUserId() + "/media")
                .queryParam("fields", "id,caption,media_type,media_url,thumbnail_url,permalink,timestamp,alt_text")
                .queryParam("access_token", auth.getAccessToken())
                .queryParam("appsecret_proof", appSecretProof)
                .toUriString();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");

            List<InstagramPostDto> posts = new ArrayList<>();

            if (data != null && data.isArray()) {
                for (JsonNode item : data) {
                    String mediaType = item.path("media_type").asText("");

                    if (!"IMAGE".equals(mediaType)
                            && !"CAROUSEL_ALBUM".equals(mediaType)
                            && !"VIDEO".equals(mediaType)) {
                        continue;
                    }

                    String id = item.path("id").asText("");
                    String caption = item.path("caption").asText("");
                    String mediaUrl = item.path("media_url").asText("");
                    String thumbnailUrl = item.path("thumbnail_url").asText("");
                    String permalink = item.path("permalink").asText("");
                    String timestamp = item.path("timestamp").asText("");
                    String altText = item.path("alt_text").asText("");

                    if (altText == null || altText.isBlank()) {
                        altText = (caption != null && !caption.isBlank())
                                ? caption
                                : "Trendz Firenze Instagram post";
                    }

                    boolean isVideo = "VIDEO".equals(mediaType);
                    boolean isCarousel = "CAROUSEL_ALBUM".equals(mediaType);

                    List<InstagramMediaItemDto> items;

                    if (isCarousel) {
                        items = fetchCarouselChildren(id, auth.getAccessToken(), appSecretProof, altText);

                        if (items.isEmpty() && mediaUrl != null && !mediaUrl.isBlank()) {
                            items.add(new InstagramMediaItemDto(
                                    id,
                                    "IMAGE",
                                    mediaUrl,
                                    thumbnailUrl,
                                    altText,
                                    false
                            ));
                        }
                    } else {
                        items = new ArrayList<>();
                        items.add(new InstagramMediaItemDto(
                                id,
                                mediaType,
                                mediaUrl,
                                thumbnailUrl,
                                altText,
                                isVideo
                        ));
                    }

                    posts.add(new InstagramPostDto(
                            id,
                            caption,
                            mediaType,
                            mediaUrl,
                            thumbnailUrl,
                            permalink,
                            timestamp,
                            altText,
                            isVideo,
                            isCarousel,
                            items
                    ));

                    if (posts.size() >= limit) {
                        break;
                    }
                }
            }

            return posts;

        } catch (HttpStatusCodeException e) {
            String message = "Failed to fetch Instagram posts";
            try {
                JsonNode errorRoot = objectMapper.readTree(e.getResponseBodyAsString());
                if (errorRoot.has("error") && errorRoot.get("error").has("message")) {
                    message = errorRoot.get("error").get("message").asText();
                }
            } catch (Exception ignored) {
            }
            throw new RuntimeException(message);

        } catch (Exception e) {
            throw new RuntimeException(
                    e.getMessage() != null ? e.getMessage() : "Unexpected Instagram fetch error"
            );
        }
    }

    private List<InstagramMediaItemDto> fetchCarouselChildren(
            String mediaId,
            String accessToken,
            String appSecretProof,
            String altText
    ) {
        List<InstagramMediaItemDto> items = new ArrayList<>();

        String childrenUrl = UriComponentsBuilder
                .fromHttpUrl("https://graph.facebook.com/" + graphVersion + "/" + mediaId + "/children")
                .queryParam("fields", "id,media_type,media_url,thumbnail_url")
                .queryParam("access_token", accessToken)
                .queryParam("appsecret_proof", appSecretProof)
                .toUriString();

        try {
            String childrenResponse = restTemplate.getForObject(childrenUrl, String.class);
            JsonNode childrenRoot = objectMapper.readTree(childrenResponse);
            JsonNode childrenData = childrenRoot.get("data");

            if (childrenData != null && childrenData.isArray()) {
                for (JsonNode child : childrenData) {
                    String childId = child.path("id").asText("");
                    String childMediaType = child.path("media_type").asText("");
                    String childMediaUrl = child.path("media_url").asText("");
                    String childThumbnailUrl = child.path("thumbnail_url").asText("");
                    boolean childVideo = "VIDEO".equals(childMediaType);

                    if (!"IMAGE".equals(childMediaType) && !"VIDEO".equals(childMediaType)) {
                        continue;
                    }

                    items.add(new InstagramMediaItemDto(
                            childId,
                            childMediaType,
                            childMediaUrl,
                            childThumbnailUrl,
                            altText,
                            childVideo
                    ));
                }
            }

            return items;

        } catch (HttpStatusCodeException e) {
            return items;
        } catch (Exception e) {
            return items;
        }
    }

    private String createAppSecretProof(String token, String appSecret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    appSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] digest = mac.doFinal(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate appsecret_proof");
        }
    }
}