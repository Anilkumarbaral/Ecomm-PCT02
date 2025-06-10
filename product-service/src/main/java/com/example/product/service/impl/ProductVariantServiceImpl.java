package com.example.product.service.impl;

import com.example.product.dto.request.ProductVariantRequest;
import com.example.product.dto.response.ProductVariantResponse;
import com.example.product.model.Product;
import com.example.product.model.ProductVariant;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.ProductVariantRepository;
import com.example.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductVariantResponse createVariant(ProductVariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.getSku())
                .price(request.getPrice())
                .comparePrice(request.getComparePrice())
                .inventoryQuantity(request.getInventoryQuantity())
                .weight(request.getWeight())
                .build();

        return mapToResponse(variantRepository.save(variant));
    }

    @Override
    public List<ProductVariantResponse> getVariantsByProductId(Long productId) {
        return variantRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteVariant(Long id) {
        variantRepository.deleteById(id);
    }

    private ProductVariantResponse mapToResponse(ProductVariant variant) {
        return ProductVariantResponse.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .sku(variant.getSku())
                .price(variant.getPrice())
                .comparePrice(variant.getComparePrice())
                .inventoryQuantity(variant.getInventoryQuantity())
                .weight(variant.getWeight())
                .createdAt(String.valueOf(variant.getCreatedAt()))
                .updatedAt(String.valueOf(variant.getUpdatedAt()))
                .build();
    }
}
