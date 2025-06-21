package com.flipdeal.demo.dto;

public record RegistrationResponseDto(
        String username,
        String email,
        boolean emailVerificationRequired
) {
}