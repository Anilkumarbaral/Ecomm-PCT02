package com.example.product.dto.request;

import lombok.Data;

@Data
public class ProductImageRequest {
    private Long productId;
    private String imageUrl;
    private String altText;
    private Integer sortOrder;
    private Boolean isPrimary;
}

