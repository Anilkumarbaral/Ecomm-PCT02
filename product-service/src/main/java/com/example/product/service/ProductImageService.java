package com.example.product.service;

import com.example.product.dto.request.ProductImageRequest;
import com.example.product.dto.response.ProductImageResponse;

import java.util.List;

public interface ProductImageService {
    ProductImageResponse addImage(ProductImageRequest request);
    List<ProductImageResponse> getImagesByProductId(Long productId);
    void deleteImage(Long id);
}
