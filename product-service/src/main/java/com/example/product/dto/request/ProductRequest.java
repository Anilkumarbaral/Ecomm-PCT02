package com.example.product.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private String shortDescription;
    private String sku;
    private BigDecimal price;
    private BigDecimal comparePrice;
    private Long categoryId;
    private String brand;
    private BigDecimal weight;
    private String dimensions;
    private String status;
    private Boolean isFeatured;
    private String metaTitle;
    private String metaDescription;
    private String sellerId;

}
