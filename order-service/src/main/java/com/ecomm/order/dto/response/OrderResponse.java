package com.ecomm.order.dto.response;


import com.ecomm.order.dto.request.OrderItemRequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderItemRequest> items;
}

