package com.flipdeal.demo.repository;

import com.flipdeal.demo.entity.OrderEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByUserId(UUID userId);
    Optional<OrderEntity> findByRazorpayOrderId(String razorpayOrderId);
    List<OrderEntity> findByPaymentStatusIgnoreCase(String paymentStatus);
    
    @EntityGraph(attributePaths = "orderedItems")
    List<OrderEntity> findAll();


}
