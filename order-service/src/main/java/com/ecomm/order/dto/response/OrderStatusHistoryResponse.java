package com.ecomm.order.dto.response;


import com.ecomm.order.model.OrderStatusHistory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {
    private Long id;
    private Long orderId;
    private String status;
    private String notes;
    private Long createdBy;
    private LocalDateTime createdAt;

    public static OrderStatusHistoryResponse fromEntity(OrderStatusHistory history) {
        return OrderStatusHistoryResponse.builder()
                .id(history.getId())
                .orderId(history.getOrder().getId())
                .status(history.getStatus())
                .notes(history.getNotes())
                .createdBy(history.getCreatedBy())
                .createdAt(history.getCreatedAt())
                .build();
    }
}

