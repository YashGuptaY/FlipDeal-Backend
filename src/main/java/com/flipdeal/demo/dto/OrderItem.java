package com.flipdeal.demo.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderItem {

    private UUID productId;
    private int quantity;
    private double price;
    private String category;
    private String imageUrl;
    private String description;
    private String name;
}


