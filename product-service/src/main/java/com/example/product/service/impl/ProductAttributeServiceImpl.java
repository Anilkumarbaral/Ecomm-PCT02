package com.example.product.service.impl;

import com.example.product.dto.request.ProductAttributeRequest;
import com.example.product.dto.response.ProductAttributeResponse;
import com.example.product.model.Product;
import com.example.product.model.ProductAttribute;
import com.example.product.repository.ProductAttributeRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductAttributeResponse createAttribute(ProductAttributeRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductAttribute attribute = ProductAttribute.builder()
                .product(product)
                .attributeName(request.getAttributeName())
                .attributeValue(request.getAttributeValue())
                .build();

        return mapToResponse(attributeRepository.save(attribute));
    }

    @Override
    public List<ProductAttributeResponse> getAttributesByProductId(Long productId) {
        return attributeRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttribute(Long id) {
        attributeRepository.deleteById(id);
    }

    private ProductAttributeResponse mapToResponse(ProductAttribute attr) {
        return ProductAttributeResponse.builder()
                .id(attr.getId())
                .productId(attr.getProduct().getId())
                .attributeName(attr.getAttributeName())
                .attributeValue(attr.getAttributeValue())
                .createdAt(String.valueOf(attr.getCreatedAt()))
                .build();
    }
}
