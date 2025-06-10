package com.example.product.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String slug;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
}

