package com.flipdeal.demo.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserProfileDto(UUID userId, String email, String username, boolean emailVerified) {

    /**
     * Custom constructor that takes only email, username, and emailVerified.
     * It calls the main constructor, passing null for the userId.
     */
    public UserProfileDto(String email, String username, boolean emailVerified) {
        this(null, email, username, emailVerified);
    }
}