package com.flipdeal.demo.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private String imageUrl;
    private double price;
    private String category;
}
