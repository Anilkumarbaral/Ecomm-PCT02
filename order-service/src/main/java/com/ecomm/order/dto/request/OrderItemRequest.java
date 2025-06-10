package com.ecomm.order.dto.request;


import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    private Long productId;
    private Long productVariantId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String productName;
    private String productSku;
}
