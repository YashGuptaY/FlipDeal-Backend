package com.flipdeal.demo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.flipdeal.demo.dto.CartRequest;
import com.flipdeal.demo.dto.CartResponse;
import com.flipdeal.demo.service.CartServiceImpl;

@RestController
@RequestMapping("/api/cart/user")
@AllArgsConstructor
@Slf4j
public class CartController {

    private final CartServiceImpl cartService;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request) {
        UUID productId = request.getProductId();
        log.info("POST /api/cart - addToCart called with productId: {}", productId);
        if (productId == null) {
            log.error("POST /api/cart - productId not found in request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId not found");
        }
        return cartService.addToCart(request);
    }

    @GetMapping
    public CartResponse getCart() {
        log.info("GET /api/cart - getCart called");
        return cartService.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        log.info("DELETE /api/cart - clearCart called");
        cartService.clearCart();
    }

    @PostMapping("/remove")
    public CartResponse removeFromCart(@RequestBody CartRequest request) {
        UUID productId = request.getProductId();
        log.info("POST /api/cart/remove - removeFromCart called with productId: {}", productId);
        if (productId == null) {
            log.error("POST /api/cart/remove - productId not found in request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId not found");
        }
        return cartService.removeFromCart(request);
    }
}
