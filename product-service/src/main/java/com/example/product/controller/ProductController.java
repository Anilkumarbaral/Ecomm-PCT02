package com.example.product.controller;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.dto.response.ProductStatisticsResponse;
import com.example.product.security.UserPrincipal;
import com.example.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    /**
     * Only ADMIN or SELLER can create products
     * SELLER can only create products for themselves
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} creating product: {}", user.getUserId(), request.getName());

        return ResponseEntity.ok(productService.createProduct(request, user));
    }

    /**
     * Anyone authenticated can view a product
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * All authenticated users can view products with pagination
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'CUSTOMER')")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        return ResponseEntity.ok(productService.getAllProducts(pageable, category, search));
    }

    /**
     * ADMIN can update any product
     * SELLER can only update their own products
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.isProductOwner(#id, authentication.principal.userId))")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} updating product: {}", user.getUserId(), id);

        return ResponseEntity.ok(productService.updateProduct(id, request, user));
    }

    /**
     * Only ADMIN can delete any product
     * SELLER can delete their own products
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @productService.isProductOwner(#id, authentication.principal.userId))")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} deleting product: {}", user.getUserId(), id);

        productService.deleteProduct(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * SELLER can view their own products
     */
    @GetMapping("/my-products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            Pageable pageable,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(productService.getProductsBySeller(user.getUserId(), pageable));
    }

    /**
     * ADMIN can view products by any seller
     */
    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponse>> getProductsBySeller(
            @PathVariable String sellerId,
            Pageable pageable) {

        return ResponseEntity.ok(productService.getProductsBySeller(sellerId, pageable));
    }

    /**
     * CUSTOMER can add products to wishlist
     */
    @PostMapping("/{id}/wishlist")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> addToWishlist(
            @PathVariable Long id,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        productService.addToWishlist(id, user.getUserId());
        return ResponseEntity.ok().build();
    }

    /**
     * CUSTOMER can remove products from wishlist
     */
    @DeleteMapping("/{id}/wishlist")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long id,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        productService.removeFromWishlist(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * ADMIN can change product status (active/inactive)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("Admin {} changing product {} status to: {}", user.getUserId(), id, active);

        return ResponseEntity.ok(productService.updateProductStatus(id, active, user));
    }

    /**
     * Get product statistics - ADMIN only
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductStatisticsResponse> getProductStatistics() {
        return ResponseEntity.ok(productService.getProductStatistics());
    }
}