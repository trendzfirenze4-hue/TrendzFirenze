

package com.mydev.ecommerce.instagram.service;

import com.mydev.ecommerce.instagram.dto.InstagramRefreshResponse;
import com.mydev.ecommerce.instagram.entity.InstagramAuth;
import com.mydev.ecommerce.instagram.repository.InstagramAuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InstagramTokenService {

    private final InstagramAuthRepository instagramAuthRepository;

    @Value("${instagram.refresh-threshold-days:15}")
    private int refreshThresholdDays;

    public InstagramTokenService(InstagramAuthRepository instagramAuthRepository) {
        this.instagramAuthRepository = instagramAuthRepository;
    }

    public Optional<InstagramAuth> getActiveAuth() {
        return instagramAuthRepository.findFirstByActiveTrueOrderByIdDesc();
    }

    public InstagramAuth getActiveAuthOrThrow() {
        return getActiveAuth()
                .orElseThrow(() -> new RuntimeException("Instagram auth record not found"));
    }

    public InstagramRefreshResponse refreshIfNeeded() {
        InstagramAuth auth = getActiveAuthOrThrow();

        if (auth.isExpired()) {
            return new InstagramRefreshResponse(
                    false,
                    "Instagram token expired. Manual reconnect required.",
                    auth.getExpiresAt(),
                    auth.getRefreshedAt(),
                    true
            );
        }

        if (!auth.shouldRefresh(refreshThresholdDays)) {
            return new InstagramRefreshResponse(
                    true,
                    "Token is healthy. No refresh needed.",
                    auth.getExpiresAt(),
                    auth.getRefreshedAt(),
                    false
            );
        }

        return forceRefresh();
    }

    public InstagramRefreshResponse forceRefresh() {
        InstagramAuth auth = getActiveAuthOrThrow();

        if (auth.isExpired()) {
            return new InstagramRefreshResponse(
                    false,
                    "Instagram token expired. Manual reconnect required.",
                    auth.getExpiresAt(),
                    auth.getRefreshedAt(),
                    true
            );
        }

        return new InstagramRefreshResponse(
                true,
                "Token is valid. No API refresh call is used for this token flow.",
                auth.getExpiresAt(),
                auth.getRefreshedAt(),
                false
        );
    }

    @Scheduled(cron = "${instagram.scheduled-refresh-cron:0 0 3 * * *}")
    public void scheduledRefresh() {
        try {
            Optional<InstagramAuth> authOpt = getActiveAuth();

            if (authOpt.isEmpty()) {
                System.out.println("Instagram token not configured yet.");
                return;
            }

            InstagramAuth auth = authOpt.get();

            if (auth.isExpired()) {
                System.err.println("Instagram token expired. Manual reconnect required.");
                return;
            }

            if (auth.shouldRefresh(refreshThresholdDays)) {
                System.out.println("Instagram token is nearing expiry. Manual reconnect recommended soon.");
            }
        } catch (Exception e) {
            System.err.println("Instagram scheduled check failed: " + e.getMessage());
        }
    }

    public InstagramAuth saveInitialLongLivedToken(String instagramUserId, String longLivedAccessToken, int expiresInSeconds) {
        InstagramAuth existing = instagramAuthRepository.findFirstByActiveTrueOrderByIdDesc().orElse(null);

        if (existing != null) {
            existing.setInstagramUserId(instagramUserId);
            existing.setAccessToken(longLivedAccessToken);
            existing.setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
            existing.setRefreshedAt(LocalDateTime.now());
            existing.setActive(true);
            return instagramAuthRepository.save(existing);
        }

        InstagramAuth auth = new InstagramAuth();
        auth.setInstagramUserId(instagramUserId);
        auth.setAccessToken(longLivedAccessToken);
        auth.setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
        auth.setRefreshedAt(LocalDateTime.now());
        auth.setActive(true);

        return instagramAuthRepository.save(auth);
    }
}