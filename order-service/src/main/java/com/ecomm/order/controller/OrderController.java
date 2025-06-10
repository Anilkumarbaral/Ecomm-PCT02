package com.ecomm.order.controller;

import com.ecomm.order.dto.request.OrderAddressRequest;
import com.ecomm.order.dto.request.OrderRequest;
import com.ecomm.order.dto.response.*;
import com.ecomm.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Creating new order for user ID: {}", orderRequest.getUserId());
        log.debug("Order request details: {}", orderRequest);

        try {
            OrderResponse response = orderService.createOrder(orderRequest);
            log.info("Order created successfully with ID: {}", response.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating order for user ID: {}. Error: {}", orderRequest.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/{orderId}/address")
    public ResponseEntity<OrderAddressResponse> addAddressToOrder(
            @PathVariable Long orderId,
            @RequestBody OrderAddressRequest addressRequest) {
        log.info("Adding address to order ID: {}", orderId);
        log.debug("Address details: {}", addressRequest);

        try {
            OrderAddressResponse response = orderService.addAddressToOrder(orderId, addressRequest);
            log.info("Address added successfully to order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error adding address to order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{orderId}/payment-method")
    public ResponseEntity<OrderResponse> selectPaymentMethod(
            @PathVariable Long orderId,
            @RequestParam String method) {
        log.info("Selecting payment method '{}' for order ID: {}", method, orderId);

        try {
            OrderResponse response = orderService.selectPaymentMethod(orderId, method);
            log.info("Payment method '{}' selected successfully for order ID: {}", method, orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error selecting payment method for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{orderId}/review")
    public ResponseEntity<OrderResponse> reviewOrder(@PathVariable Long orderId) {
        log.info("Reviewing order ID: {}", orderId);

        try {
            OrderResponse response = orderService.reviewOrder(orderId);
            log.debug("Order review details for ID {}: {}", orderId, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reviewing order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable Long orderId) {
        log.info("Confirming order ID: {}", orderId);

        try {
            OrderResponse response = orderService.confirmOrder(orderId);
            log.info("Order ID: {} confirmed successfully", orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error confirming order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<OrderResponse>> getOrderHistoryByUser(@PathVariable Long userId) {
        log.info("Fetching order history for user ID: {}", userId);

        try {
            List<OrderResponse> response = orderService.getOrderHistoryByUser(userId);
            log.info("Found {} orders for user ID: {}", response.size(), userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching order history for user ID: {}. Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusHistoryResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status,
            @RequestParam Long updatedBy) {
        log.info("Updating status to '{}' for order ID: {} by user ID: {}", status, orderId, updatedBy);

        try {
            OrderStatusHistoryResponse response = orderService.updateOrderStatus(orderId, status, updatedBy);
            log.info("Status updated successfully for order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating status for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<InvoiceResponse> generateInvoice(@PathVariable Long orderId) {
        log.info("Generating invoice for order ID: {}", orderId);

        try {
            InvoiceResponse response = orderService.generateInvoice(orderId);
            log.info("Invoice generated successfully for order ID: {}", orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating invoice for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }
}