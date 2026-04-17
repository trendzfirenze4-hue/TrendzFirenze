


package com.mydev.ecommerce.instagram.controller;

import com.mydev.ecommerce.instagram.dto.InstagramRefreshResponse;
import com.mydev.ecommerce.instagram.entity.InstagramAuth;
import com.mydev.ecommerce.instagram.service.InstagramTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/instagram")
public class InstagramAdminController {

    private final InstagramTokenService instagramTokenService;

    @Value("${app.instagram.admin-refresh-secret}")
    private String adminRefreshSecret;

    public InstagramAdminController(InstagramTokenService instagramTokenService) {
        this.instagramTokenService = instagramTokenService;
    }

    private boolean isUnauthorized(String secret) {
        System.out.println("RECEIVED SECRET = [" + secret + "]");
        System.out.println("EXPECTED SECRET = [" + adminRefreshSecret + "]");
        return secret == null || adminRefreshSecret == null || !secret.equals(adminRefreshSecret);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @RequestHeader(value = "X-Admin-Refresh-Secret", required = false) String secret
    ) {
        if (isUnauthorized(secret)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        InstagramRefreshResponse response = instagramTokenService.forceRefresh();

        if (!response.isOk()) {
            return ResponseEntity.status(response.isNeedsReconnect() ? 400 : 500).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-token")
    public ResponseEntity<?> updateToken(
            @RequestHeader(value = "X-Admin-Refresh-Secret", required = false) String secret,
            @RequestBody Map<String, String> body
    ) {
        if (isUnauthorized(secret)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String newToken = body.get("accessToken");

        if (newToken == null || newToken.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "accessToken required"));
        }

        InstagramAuth auth = instagramTokenService.getActiveAuthOrThrow();

        instagramTokenService.saveInitialLongLivedToken(
                auth.getInstagramUserId(),
                newToken.trim(),
                60 * 24 * 60 * 60
        );

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "Instagram token updated successfully"
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(
            @RequestHeader(value = "X-Admin-Refresh-Secret", required = false) String secret
    ) {
        if (isUnauthorized(secret)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        Optional<InstagramAuth> authOpt = instagramTokenService.getActiveAuth();

        if (authOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "ok", true,
                    "configured", false,
                    "message", "Instagram token not configured yet"
            ));
        }

        InstagramAuth auth = authOpt.get();

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "configured", true,
                "instagramUserId", auth.getInstagramUserId(),
                "expiresAt", auth.getExpiresAt(),
                "refreshedAt", auth.getRefreshedAt(),
                "active", auth.isActive(),
                "expired", auth.isExpired()
        ));
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "Instagram admin controller working"
        ));
    }
}