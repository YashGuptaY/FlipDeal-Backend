package com.flipdeal.demo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.flipdeal.demo.dto.OrderItem;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    private UUID userId;

    private String userAddress;

    private String phoneNumber;

    private String email;

    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItem> orderedItems;

    private double amount;

    private String paymentStatus;

    private String razorpayOrderId;

    private String razorpaySignature;

    private String razorpayPaymentId;

    private String orderStatus;
}
