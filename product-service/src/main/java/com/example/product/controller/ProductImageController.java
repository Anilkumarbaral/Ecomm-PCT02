package com.example.product.controller;

import com.example.product.dto.request.ProductImageRequest;
import com.example.product.dto.response.ProductImageResponse;
import com.example.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;

    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(@RequestBody ProductImageRequest request) {
        return ResponseEntity.ok(imageService.addImage(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductImageResponse>> getImagesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(imageService.getImagesByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
