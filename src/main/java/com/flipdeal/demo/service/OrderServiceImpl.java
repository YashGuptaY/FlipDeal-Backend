package com.flipdeal.demo.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.flipdeal.demo.dto.OrderRequest;
import com.flipdeal.demo.dto.OrderResponse;
import com.flipdeal.demo.entity.OrderEntity;
import com.flipdeal.demo.repository.CartRespository;
import com.flipdeal.demo.repository.OrderRepository;
import com.flipdeal.demo.userservice.UserService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CartRespository cartRespository;

    @Value("${razorpay_key}")
    private String RAZORPAY_KEY;
    @Value("${razorpay_secret}")
    private String RAZORPAY_SECRET;

    public OrderResponse createOrderWithPayment(OrderRequest request) throws RazorpayException {
        OrderEntity newOrder = convertToEntity(request);
        UUID loggedInUserId = userService.findByUserId();
        newOrder.setUserId(loggedInUserId);
        log.info("Creating order with payment for userId: {}",loggedInUserId);
        newOrder = orderRepository.save(newOrder);
        log.info("Order entity saved: {}", newOrder.getId());
        // create razorpay payment order
        RazorpayClient razorpayClient = new RazorpayClient(RAZORPAY_KEY, RAZORPAY_SECRET);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", newOrder.getAmount() * 100);
        orderRequest.put("currency", "INR");
        orderRequest.put("payment_capture", 1);
        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        log.info("Razorpay order created: {}", String.valueOf(razorpayOrder.get("id")));
        newOrder.setRazorpayOrderId(razorpayOrder.get("id"));
        newOrder = orderRepository.save(newOrder);
        log.info("Order updated with Razorpay orderId: {}", newOrder.getRazorpayOrderId());
        return convertToResponse(newOrder);
    }

    public void verifyPayment(Map<String, String> paymentData, String status) {
        log.info("Verifying payment for order: {} with status: {}", paymentData.get("razorpay_order_id"), status);
        String razorpayOrderId = paymentData.get("razorpay_order_id");
        OrderEntity existingOrder = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> {
                    log.error("Order not found for Razorpay orderId: {}", razorpayOrderId);
                    return new RuntimeException("Order not found");
                });
        existingOrder.setPaymentStatus(status);
        existingOrder.setRazorpaySignature(paymentData.get("razorpay_signature"));
        existingOrder.setRazorpayPaymentId(paymentData.get("razorpay_payment_id"));
        orderRepository.save(existingOrder);
        log.info("Order payment status updated for order: {}", razorpayOrderId);
        if ("paid".equalsIgnoreCase(status)) {
            cartRespository.deleteByUserId(existingOrder.getUserId());
            log.info("Cart cleared for user: {}", existingOrder.getUserId());
        }
    }

    public List<OrderResponse> getUserOrders() {
        UUID loggedInUserId = userService.findByUserId();
        log.info("Fetching orders for user: {}", loggedInUserId);
        List<OrderEntity> list = orderRepository.findByUserId(loggedInUserId);
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    public void removeOrder(UUID orderId) {
        log.info("Removing order: {}", orderId);
        orderRepository.deleteById(orderId);
    }

    public List<OrderResponse> getOrdersOfAllUsers() {
        log.info("Fetching orders for all users");
        List<OrderEntity> list = orderRepository.findAll();
        return list.stream().map(entity -> convertToResponse(entity)).collect(Collectors.toList());
    }

    public void updateOrderStatus(UUID orderId, String status) {
        log.info("Updating order status for order: {} to status: {}", orderId, status);
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new RuntimeException("Order not found");
                });
        entity.setOrderStatus(status);
        orderRepository.save(entity);
        log.info("Order status updated for order: {}", orderId);
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userAddress(newOrder.getUserAddress())
                .userId(newOrder.getUserId())
                .razorpayOrderId(newOrder.getRazorpayOrderId())
                .paymentStatus(newOrder.getPaymentStatus())
                .orderStatus(newOrder.getOrderStatus())
                .email(newOrder.getEmail())
                .phoneNumber(newOrder.getPhoneNumber())
                .orderedItems(newOrder.getOrderedItems())
                .build();
    }

    private OrderEntity convertToEntity(OrderRequest request) {
        return OrderEntity.builder()
                .userAddress(request.getUserAddress())
                .amount(request.getAmount())
                .orderedItems(request.getOrderedItems())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .orderStatus(request.getOrderStatus())
                .build();
    }
}
