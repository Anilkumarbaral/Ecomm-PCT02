package com.example.product.dto.response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageResponse {
    private Long id;
    private Long productId;
    private String imageUrl;
    private String altText;
    private Integer sortOrder;
    private Boolean isPrimary;
    private String createdAt;
}

