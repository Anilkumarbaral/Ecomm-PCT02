package com.example.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductVariantResponse {
    private Long id;
    private Long productId;
    private String sku;
    private BigDecimal price;
    private BigDecimal comparePrice;
    private Integer inventoryQuantity;
    private BigDecimal weight;
    private String createdAt;
    private String updatedAt;
}
