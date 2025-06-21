package com.flipdeal.demo.dto;

import java.util.UUID;

public record AuthenticationResponseDto(String accessToken, UUID refreshToken) {
}