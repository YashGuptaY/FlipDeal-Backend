package com.flipdeal.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    private UUID id;
    private UUID userId;
    private Map<UUID, Integer> items = new HashMap<>();
}
