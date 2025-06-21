package com.flipdeal.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private String name;
    private String description;
    private Boolean isAvailable;
    private double price;
    private String category;
}
