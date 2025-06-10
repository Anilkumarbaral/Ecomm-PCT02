package com.example.product.service.impl;

import com.example.product.dto.request.ProductImageRequest;
import com.example.product.dto.response.ProductImageResponse;
import com.example.product.model.Product;
import com.example.product.model.ProductImage;
import com.example.product.repository.ProductImageRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductImageResponse addImage(ProductImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(request.getImageUrl())
                .altText(request.getAltText())
                .sortOrder(request.getSortOrder())
                .isPrimary(request.getIsPrimary())
                .build();

        return mapToResponse(imageRepository.save(image));
    }

    @Override
    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        return imageRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteImage(Long id) {
        imageRepository.deleteById(id);
    }

    private ProductImageResponse mapToResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .altText(image.getAltText())
                .sortOrder(image.getSortOrder())
                .isPrimary(image.getIsPrimary())
                .createdAt(String.valueOf(image.getCreatedAt()))
                .build();
    }
}

