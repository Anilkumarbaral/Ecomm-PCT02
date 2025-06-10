package com.example.product.dto.response;


import lombok.Data;
import java.util.List;

@Data
public class ProductStatisticsResponse {
    private long totalProducts;
    private long activeProducts;
    private long inactiveProducts;
    private long featuredProducts;
    private List<Object[]> categoryStatistics;
}
