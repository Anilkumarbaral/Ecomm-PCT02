package com.example.product.controller;


import com.example.product.dto.request.WishlistRequest;
import com.example.product.dto.response.WishlistResponse;
import com.example.product.security.UserPrincipal;
import com.example.product.service.WishlistService;
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
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Add product to wishlist - CUSTOMER only
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WishlistResponse> addToWishlist(
            @Valid @RequestBody WishlistRequest request,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} adding product {} to wishlist", user.getUserId(), request.getProductId());

        WishlistResponse response = wishlistService.addToWishlist(request, user.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Remove product from wishlist - CUSTOMER only
     */
    @DeleteMapping("/product/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} removing product {} from wishlist", user.getUserId(), productId);

        wishlistService.removeFromWishlist(productId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user's wishlist with pagination - CUSTOMER only
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<WishlistResponse>> getUserWishlist(
            Pageable pageable,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Page<WishlistResponse> wishlist = wishlistService.getUserWishlist(user.getUserId(), pageable);

        return ResponseEntity.ok(wishlist);
    }

    /**
     * Get user's wishlist by priority - CUSTOMER only
     */
    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<WishlistResponse>> getUserWishlistByPriority(
            @PathVariable Integer priority,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        List<WishlistResponse> wishlist = wishlistService.getUserWishlistByPriority(user.getUserId(), priority);

        return ResponseEntity.ok(wishlist);
    }

    /**
     * Update wishlist item - CUSTOMER only
     */
    @PutMapping("/{wishlistId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WishlistResponse> updateWishlistItem(
            @PathVariable Long wishlistId,
            @Valid @RequestBody WishlistRequest request,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} updating wishlist item {}", user.getUserId(), wishlistId);

        WishlistResponse response = wishlistService.updateWishlistItem(wishlistId, request, user.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Check if product is in wishlist - CUSTOMER only
     */
    @GetMapping("/check/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Boolean> isProductInWishlist(
            @PathVariable Long productId,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        boolean inWishlist = wishlistService.isProductInWishlist(productId, user.getUserId());

        return ResponseEntity.ok(inWishlist);
    }

    /**
     * Get wishlist count - CUSTOMER only
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Long> getWishlistCount(Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        long count = wishlistService.getWishlistCount(user.getUserId());

        return ResponseEntity.ok(count);
    }

    /**
     * Clear entire wishlist - CUSTOMER only
     */
    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearWishlist(Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        log.info("User {} clearing entire wishlist", user.getUserId());

        wishlistService.clearWishlist(user.getUserId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get specific wishlist item - CUSTOMER only
     */
    @GetMapping("/{wishlistId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<WishlistResponse> getWishlistItem(
            @PathVariable Long wishlistId,
            Authentication authentication) {

        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        WishlistResponse response = wishlistService.getWishlistItem(wishlistId, user.getUserId());

        return ResponseEntity.ok(response);
    }
}
