package com.example.product.dto.request;

import lombok.Data;

@Data
public class ProductAttributeRequest {
    private Long productId;
    private String attributeName;
    private String attributeValue;
}

