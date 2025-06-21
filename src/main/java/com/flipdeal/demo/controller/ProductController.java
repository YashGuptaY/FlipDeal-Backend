package com.flipdeal.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipdeal.demo.dto.ProductRequest;
import com.flipdeal.demo.dto.ProductResponse;
import com.flipdeal.demo.dto.ProductUpdateReq;
import com.flipdeal.demo.service.ProductServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@CrossOrigin("*")
@Slf4j
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping
    public ProductResponse addProduct(@RequestPart("product") String productString,
            @RequestPart("file") MultipartFile file) {
        log.info("POST /api/products - addProduct called");
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequest request = null;
        try {
            request = objectMapper.readValue(productString, ProductRequest.class);
        } catch (JsonProcessingException ex) {
            log.error("POST /api/products - Invalid JSON format");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON format");
        }
        ProductResponse response = productService.addProduct(request, file);
        return response;
    }

    @GetMapping
    public List<ProductResponse> readProducts() {
        log.info("GET /api/products - readProducts called");
        return productService.readProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse readProduct(@PathVariable UUID id) {
        log.info("GET /api/products/{} - readProduct called", id);
        return productService.readProduct(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable UUID id) {
        log.info("DELETE /api/products/{} - deleteProduct called", id);
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.info("GET /api/products/search - searchProducts called with keyword: {}", keyword);
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @GetMapping("/category")
    public List<ProductResponse> getProductsByCategory(@RequestParam String category) {
        log.info("GET /api/products/category - getProductsByCategory called with category: {}", category);
        return productService.getProductsByCategory(category);
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<ProductResponse> updateAvailability(
            @PathVariable UUID id,
            @RequestBody Boolean isAvailable) {
        log.info("PATCH /api/products/{}/availability - updateAvailability called with isAvailable: {}", id,
                isAvailable);
        return ResponseEntity.ok(productService.updateProductAvailability(id, isAvailable));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<ProductUpdateReq> updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductUpdateReq productUpdateReq) {
        log.info("PATCH /api/products/{}/update - updateProduct called", id);
        return ResponseEntity.ok(productService.updateProduct(id, productUpdateReq));
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<ProductResponse>> searchProductsByCategory(
            @RequestParam String keyword,
            @RequestParam String category) {
        log.info("GET /api/products/search/category - searchProductsByCategory called with keyword: {}, category: {}",
                keyword, category);
        return ResponseEntity.ok(productService.searchProductsByCategory(keyword, category));
    }

}
