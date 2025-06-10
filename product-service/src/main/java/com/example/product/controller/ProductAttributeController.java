package com.example.product.controller;

import com.example.product.dto.request.ProductAttributeRequest;
import com.example.product.dto.response.ProductAttributeResponse;
import com.example.product.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    @PostMapping
    public ResponseEntity<ProductAttributeResponse> createAttribute(@RequestBody ProductAttributeRequest request) {
        return ResponseEntity.ok(attributeService.createAttribute(request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductAttributeResponse>> getAttributesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(attributeService.getAttributesByProductId(productId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }
}

