package com.flipdeal.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flipdeal.demo.entity.OrderEntity;
import com.flipdeal.demo.repository.OrderRepository;
import com.flipdeal.demo.repository.ProductRepository;
import com.flipdeal.demo.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class DashboardServiceImpl {

	private final UserRepository userRepository;

	private final OrderRepository orderRepository;

	private final ProductRepository productRepository;

	public long getUserCount() {
		log.info("DashboardServiceImpl: getUserCount called");
		return userRepository.count();
	}

	public long getOrderCount() {
		log.info("DashboardServiceImpl: getOrderCount called");
		return orderRepository.count();
	}

	public long getProductCount() {
		log.info("DashboardServiceImpl: getProductCount called");
		return productRepository.count();
	}

	public double getTotalRevenue() {
		log.info("DashboardServiceImpl: getTotalRevenue called");
		List<OrderEntity> paidOrders = orderRepository.findByPaymentStatusIgnoreCase("paid");
		return paidOrders.stream()
				.mapToDouble(OrderEntity::getAmount)
				.sum();
	}

}
