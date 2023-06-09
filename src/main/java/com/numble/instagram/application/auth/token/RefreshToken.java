package com.numble.instagram.application.auth.token;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RefreshToken(String tokenValue, Long userId, LocalDateTime expiredAt) {

    public static RefreshToken create(Long userId, long validTimeInDays) {
        return new RefreshToken(UUID.randomUUID().toString(), userId, LocalDateTime.now().plusDays(validTimeInDays));
    }

    public boolean isExpired() {
        return expiredAt.isBefore(LocalDateTime.now());
    }
}
