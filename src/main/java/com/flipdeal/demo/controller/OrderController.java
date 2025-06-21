package com.flipdeal.demo.controller;

import com.razorpay.RazorpayException;
import com.flipdeal.demo.dto.OrderRequest;
import com.flipdeal.demo.dto.OrderResponse;
import com.flipdeal.demo.service.OrderServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest request)
            throws RazorpayException {
        log.info("POST /api/orders/create - createOrderWithPayment called for userId: {}");
        return orderService.createOrderWithPayment(request);
    }

    @PostMapping("/verify")
    public void verifyPayment(@RequestBody Map<String, String> paymentData) {
        log.info("POST /api/orders/verify - verifyPayment called for order: {}", paymentData.get("razorpay_order_id"));
        orderService.verifyPayment(paymentData, "Paid");
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        log.info("GET /api/orders - getOrders called");
        return orderService.getUserOrders();
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable UUID orderId) {
        log.info("DELETE /api/orders/{} - deleteOrder called", orderId);
        orderService.removeOrder(orderId);
    }

    // admin panel
    @GetMapping("/all")
    public List<OrderResponse> getOrdersOfAllUsers() {
        log.info("GET /api/orders/all - getOrdersOfAllUsers called");
        return orderService.getOrdersOfAllUsers();
    }

    // admin panel
    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable UUID orderId, @RequestParam String status) {
        log.info("PATCH /api/orders/status/{} - updateOrderStatus called with status: {}", orderId, status);
        orderService.updateOrderStatus(orderId, status);
    }
}
