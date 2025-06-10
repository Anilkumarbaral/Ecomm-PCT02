package com.example.product.dto.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private String name;
    private String description;
    private Long parentId;
    private String slug;
    private Boolean isActive;
}

