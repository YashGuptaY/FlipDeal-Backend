package com.flipdeal.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipdeal.demo.service.DashboardServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardServiceImpl dashboardService;

    @GetMapping("/countuser")
    public ResponseEntity<Long> getUserCount() {
        log.info("GET /api/dashboard/countuser - getUserCount called");
        long count = dashboardService.getUserCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/countorder")
    public ResponseEntity<Long> getOrderCount() {
        log.info("GET /api/dashboard/countorder - getOrderCount called");
        long count = dashboardService.getOrderCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/countproduct")
    public ResponseEntity<Long> getProductCount() {
        log.info("GET /api/dashboard/countproduct - getProductCount called");
        long count = dashboardService.getProductCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        log.info("GET /api/dashboard/revenue - getTotalRevenue called");
        double totalRevenue = dashboardService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

}
