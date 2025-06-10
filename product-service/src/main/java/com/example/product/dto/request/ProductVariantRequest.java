package com.example.product.dto.request;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantRequest {
    private Long productId;
    private String sku;
    private BigDecimal price;
    private BigDecimal comparePrice;
    private Integer inventoryQuantity;
    private BigDecimal weight;
}

