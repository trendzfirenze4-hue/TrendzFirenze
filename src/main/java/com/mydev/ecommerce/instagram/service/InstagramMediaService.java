

package com.mydev.ecommerce.instagram.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydev.ecommerce.instagram.dto.InstagramPostDto;
import com.mydev.ecommerce.instagram.entity.InstagramAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

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

        String fields = String.join(",",
                "id",
                "caption",
                "media_type",
                "media_url",
                "thumbnail_url",
                "permalink",
                "timestamp",
                "alt_text"
        );

        String appSecretProof = createAppSecretProof(auth.getAccessToken(), appSecret);

        String url = "https://graph.facebook.com/" + graphVersion + "/" + auth.getInstagramUserId() + "/media" +
                "?fields=" + fields +
                "&access_token=" + auth.getAccessToken() +
                "&appsecret_proof=" + appSecretProof;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");

            List<InstagramPostDto> posts = new ArrayList<>();

            if (data != null && data.isArray()) {
                for (JsonNode item : data) {
                    String mediaType = item.path("media_type").asText("");

                    // Allow images, carousels, and videos/reels
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

                    posts.add(new InstagramPostDto(
                            id,
                            caption,
                            mediaType,
                            mediaUrl,
                            thumbnailUrl,
                            permalink,
                            timestamp,
                            altText,
                            isVideo
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