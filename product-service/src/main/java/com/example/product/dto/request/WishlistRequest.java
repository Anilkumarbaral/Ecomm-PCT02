package com.example.product.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class WishlistRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Min(value = 1, message = "Priority must be between 1 and 3")
    @Max(value = 3, message = "Priority must be between 1 and 3")
    private Integer priority = 1;
}
