package com.example.product.dto.response;

import lombok.Data;

@Data
public class WishlistResponse {

    private Long id;
    private String userId;
    private Long productId;
    private String productName;
    private String productSku;
    private Double productPrice;
    private String productBrand;
    private String productImage;
    private String productStatus;
    private String categoryName;
    private String notes;
    private Integer priority;
    private String priorityLabel;
    private String createdAt;

    // Helper method to get priority label
    public String getPriorityLabel() {
        if (priority == null) return "Low";
        switch (priority) {
            case 1: return "Low";
            case 2: return "Medium";
            case 3: return "High";
            default: return "Low";
        }
    }
}