



package com.mydev.ecommerce.email.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String BREVO_SEND_EMAIL_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient = RestClient.create();

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${app.mail.from-email}")
    private String fromEmail;

    @Value("${app.mail.from-name:Trendz Firenze}")
    private String fromName;

    @Value("${app.mail.brevo.api-key}")
    private String brevoApiKey;

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        log.info("🔥 MAIL FLOW START -> enabled={}, to={}, subject={}", mailEnabled, to, subject);

        if (!mailEnabled) {
            log.warn("❌ Mail disabled. Skipping email to {}", to);
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of(
                            "name", fromName,
                            "email", fromEmail
                    ),
                    "to", List.of(
                            Map.of("email", to)
                    ),
                    "subject", subject,
                    "htmlContent", htmlBody
            );

            String response = restClient.post()
                    .uri(BREVO_SEND_EMAIL_URL)
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("✅ EMAIL SENT SUCCESS -> to={}, response={}", to, response);

        } catch (Exception e) {
            log.error("❌ EMAIL FAILED -> to={}, reason={}", to, e.getMessage(), e);
        }
    }
}