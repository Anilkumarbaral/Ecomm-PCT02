package com.example.product.service;

import com.example.product.dto.request.ProductVariantRequest;
import com.example.product.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {
    ProductVariantResponse createVariant(ProductVariantRequest request);
    List<ProductVariantResponse> getVariantsByProductId(Long productId);
    void deleteVariant(Long id);
}
