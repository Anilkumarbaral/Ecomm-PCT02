package com.example.product.service;

import com.example.product.dto.request.ProductAttributeRequest;
import com.example.product.dto.response.ProductAttributeResponse;

import java.util.List;

public interface ProductAttributeService {
    ProductAttributeResponse createAttribute(ProductAttributeRequest request);
    List<ProductAttributeResponse> getAttributesByProductId(Long productId);
    void deleteAttribute(Long id);
}

