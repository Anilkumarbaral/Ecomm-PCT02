package com.example.product.controller;

import com.example.product.dto.request.ProductVariantRequest;
import com.example.product.dto.response.ProductVariantResponse;
import com.example.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @PostMapping
    public ResponseEntity<ProductVariantResponse> createVariant(@RequestBody ProductVariantRequest request) {
        return ResponseEntity.ok(variantService.createVariant(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductVariantResponse>> getVariantsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getVariantsByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariant(@PathVariable Long id) {
        variantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
}

