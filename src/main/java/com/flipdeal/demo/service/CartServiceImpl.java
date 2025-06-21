package com.flipdeal.demo.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.flipdeal.demo.dto.CartRequest;
import com.flipdeal.demo.dto.CartResponse;
import com.flipdeal.demo.entity.CartEntity;
import com.flipdeal.demo.repository.CartRespository;
import com.flipdeal.demo.userservice.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CartServiceImpl {

    private final CartRespository cartRespository;
    private final UserService userService;

    public CartResponse addToCart(CartRequest request) {
        UUID loggedInUserId = userService.findByUserId();
        UUID productId = request.getProductId();
        log.info("CartServiceImpl: addToCart called for userId: {}, productId: {}", loggedInUserId, productId);
        Optional<CartEntity> cartOptional = cartRespository.findByUserId(loggedInUserId);
        CartEntity cart = cartOptional.orElseGet(() -> new CartEntity(loggedInUserId, new HashMap<>()));
        Map<UUID, Integer> cartItems = cart.getItems();
        cartItems.put(productId, cartItems.getOrDefault(productId, 0) + 1);

        cart.setItems(cartItems);
        cart = cartRespository.save(cart);
        return convertToResponse(cart);
    }

    public CartResponse getCart() {
        UUID loggedInUserId = userService.findByUserId();
        log.info("CartServiceImpl: getCart called for userId: {}", loggedInUserId);
        CartEntity entity = cartRespository.findByUserId(loggedInUserId)
                .orElse(new CartEntity(null, loggedInUserId, new HashMap<>()));
        return convertToResponse(entity);
    }

    public void clearCart() {
        UUID loggedInUserId = userService.findByUserId();
        log.info("CartServiceImpl: clearCart called for userId: {}", loggedInUserId);
        cartRespository.deleteByUserId(loggedInUserId);
    }
    
    public CartResponse removeFromCart(CartRequest cartRequest) {
        UUID loggedInUserId = userService.findByUserId();
        UUID productId = cartRequest.getProductId();
        log.info("CartServiceImpl: removeFromCart called for userId: {}, productId: {}", loggedInUserId, productId);
        CartEntity entity = cartRespository.findByUserId(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("Cart is not found"));
        Map<UUID, Integer> cartItems = entity.getItems();
        if (cartItems.containsKey(productId)) {
            int currentQty = cartItems.get(productId);
            if (currentQty > 0) {
                cartItems.put(productId, currentQty - 1);
            } else {
                cartItems.remove(productId);
            }
            entity = cartRespository.save(entity);
        }
        return convertToResponse(entity);
    }

    private CartResponse convertToResponse(CartEntity cartEntity) {
        Map<UUID, Integer> itemsCopy = new HashMap<>(cartEntity.getItems());
        return CartResponse.builder()
                .id(cartEntity.getId())
                .userId(cartEntity.getUserId())
                .items(itemsCopy)
                .build();
    }
}
