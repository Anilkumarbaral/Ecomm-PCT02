package com.example.product.service;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;

import java.util.List;


import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.dto.response.ProductStatisticsResponse;
import com.example.product.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request, UserPrincipal user);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> getAllProducts(Pageable pageable, String category, String search);

    ProductResponse updateProduct(Long id, ProductRequest request, UserPrincipal user);

    void deleteProduct(Long id, UserPrincipal user);

    Page<ProductResponse> getProductsBySeller(String sellerId, Pageable pageable);

    void addToWishlist(Long productId, String userId);

    void removeFromWishlist(Long productId, String userId);

    ProductResponse updateProductStatus(Long id, boolean active, UserPrincipal user);

    ProductStatisticsResponse getProductStatistics();

    boolean isProductOwner(Long productId, String userId);
}