package com.ecomm.order.service.impl;

import com.ecomm.order.dto.request.OrderAddressRequest;
import com.ecomm.order.dto.request.OrderItemRequest;
import com.ecomm.order.dto.request.OrderRequest;
import com.ecomm.order.dto.response.*;
import com.ecomm.order.model.Order;
import com.ecomm.order.model.OrderAddress;
import com.ecomm.order.model.OrderItem;
import com.ecomm.order.model.OrderStatusHistory;
import com.ecomm.order.repository.OrderAddressRepository;
import com.ecomm.order.repository.OrderRepository;
import com.ecomm.order.repository.OrderStatusHistoryRepository;
import com.ecomm.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for user ID: {}", request.getUserId());
        log.debug("Order request details: {}", request);

        try {
            BigDecimal total = request.getSubtotal()
                    .add(request.getTaxAmount())
                    .add(request.getShippingAmount())
                    .subtract(request.getDiscountAmount());
            log.debug("Calculated order total: {}", total);

            Order order = Order.builder()
                    .orderNumber(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .status("PENDING")
                    .subtotal(request.getSubtotal())
                    .taxAmount(request.getTaxAmount())
                    .shippingAmount(request.getShippingAmount())
                    .discountAmount(request.getDiscountAmount())
                    .totalAmount(total)
                    .currency(Optional.ofNullable(request.getCurrency()).orElse("USD"))
                    .paymentStatus("PENDING")
                    .notes(request.getNotes())
                    .build();

            List<OrderItem> items = request.getItems().stream().map(item -> {
                log.debug("Processing order item for product ID: {}", item.getProductId());
                return OrderItem.builder()
                        .order(order)
                        .productId(item.getProductId())
                        .productVariantId(item.getProductVariantId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .build();
            }).collect(Collectors.toList());

            order.setItems(items);
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully with ID: {}", savedOrder.getId());

            return mapToResponse(savedOrder);
        } catch (Exception e) {
            log.error("Failed to create order for user ID: {}. Error: {}", request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse reviewOrder(Long orderId) {
        log.info("Reviewing order with ID: {}", orderId);
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new RuntimeException("Order not found");
                    });
            log.debug("Retrieved order details for review: {}", order);
            return mapToResponse(order);
        } catch (Exception e) {
            log.error("Error reviewing order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse confirmOrder(Long orderId) {
        log.info("Confirming order with ID: {}", orderId);
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new RuntimeException("Order not found");
                    });

            order.setStatus("CONFIRMED");
            order.setPaymentStatus("PAID");
            Order confirmedOrder = orderRepository.save(order);
            log.info("Order ID: {} confirmed successfully", orderId);

            OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                    .order(order)
                    .status("CONFIRMED")
                    .createdBy(order.getUserId())
                    .createdAt(LocalDateTime.now())
                    .build();

            orderStatusHistoryRepository.save(statusHistory);
            log.debug("Status history recorded for order ID: {}", orderId);

            return mapToResponse(confirmedOrder);
        } catch (Exception e) {
            log.error("Error confirming order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderAddressResponse addAddressToOrder(Long orderId, OrderAddressRequest request) {
        log.info("Adding address to order ID: {}", orderId);
        log.debug("Address details: {}", request);

        try {
            OrderAddress address = OrderAddress.builder()
                    .orderId(orderId)
                    .addressType(request.getAddressType())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .company(request.getCompany())
                    .addressLine1(request.getAddressLine1())
                    .addressLine2(request.getAddressLine2())
                    .city(request.getCity())
                    .state(request.getState())
                    .postalCode(request.getPostalCode())
                    .country(request.getCountry())
                    .phone(request.getPhone())
                    .build();

            OrderAddress saved = orderAddressRepository.save(address);
            log.info("Address added successfully to order ID: {}", orderId);

            return OrderAddressResponse.fromEntity(saved);
        } catch (Exception e) {
            log.error("Error adding address to order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse selectPaymentMethod(Long orderId, String paymentMethod) {
        log.info("Selecting payment method '{}' for order ID: {}", paymentMethod, orderId);

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new RuntimeException("Order not found");
                    });

            String paymentStatus = "PAYMENT_METHOD: " + paymentMethod.toUpperCase();
            order.setPaymentStatus(paymentStatus);
            Order updatedOrder = orderRepository.save(order);
            log.info("Payment method '{}' set successfully for order ID: {}", paymentMethod, orderId);

            return mapToResponse(updatedOrder);
        } catch (Exception e) {
            log.error("Error selecting payment method for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<OrderResponse> getOrderHistoryByUser(Long userId) {
        log.info("Fetching order history for user ID: {}", userId);

        try {
            List<Order> orders = orderRepository.findAll().stream()
                    .filter(o -> o.getUserId().equals(userId))
                    .collect(Collectors.toList());

            log.info("Found {} orders for user ID: {}", orders.size(), userId);
            return orders.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching order history for user ID: {}. Error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderStatusHistoryResponse updateOrderStatus(Long orderId, String newStatus, Long updatedBy) {
        log.info("Updating status to '{}' for order ID: {} by user ID: {}", newStatus, orderId, updatedBy);

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new RuntimeException("Order not found");
                    });

            order.setStatus(newStatus);
            orderRepository.save(order);
            log.debug("Order status updated in main record");

            OrderStatusHistory status = OrderStatusHistory.builder()
                    .order(order)
                    .status(newStatus)
                    .createdBy(updatedBy)
                    .createdAt(LocalDateTime.now())
                    .build();

            OrderStatusHistory savedStatus = orderStatusHistoryRepository.save(status);
            log.info("Status updated successfully for order ID: {}", orderId);

            return OrderStatusHistoryResponse.fromEntity(savedStatus);
        } catch (Exception e) {
            log.error("Error updating status for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public InvoiceResponse generateInvoice(Long orderId) {
        log.info("Generating invoice for order ID: {}", orderId);

        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("Order not found with ID: {}", orderId);
                        return new RuntimeException("Order not found");
                    });

            InvoiceResponse invoice = InvoiceResponse.builder()
                    .orderNumber(order.getOrderNumber())
                    .date(order.getCreatedAt())
                    .totalAmount(order.getTotalAmount())
                    .status(order.getStatus())
                    .items(order.getItems().stream().map(item ->
                            InvoiceItemResponse.builder()
                                    .name(item.getProductName())
                                    .price(item.getUnitPrice())
                                    .qty(item.getQuantity())
                                    .total(item.getTotalPrice())
                                    .build()
                    ).toList())
                    .build();

            log.info("Invoice generated successfully for order ID: {}", orderId);
            return invoice;
        } catch (Exception e) {
            log.error("Error generating invoice for order ID: {}. Error: {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    private OrderResponse mapToResponse(Order order) {
        log.debug("Mapping order entity to response for order ID: {}", order.getId());
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .paymentStatus(order.getPaymentStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item ->
                        OrderItemRequest.builder()
                                .productId(item.getProductId())
                                .productVariantId(item.getProductVariantId())
                                .productName(item.getProductName())
                                .productSku(item.getProductSku())
                                .unitPrice(item.getUnitPrice())
                                .quantity(item.getQuantity())
                                .build()
                ).toList())
                .build();
    }
}