package com.example.product.service;


import com.example.product.dto.request.WishlistRequest;
import com.example.product.dto.response.WishlistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WishlistService {

    /**
     * Add product to user's wishlist
     */
    WishlistResponse addToWishlist(WishlistRequest request, String userId);

    /**
     * Remove product from user's wishlist
     */
    void removeFromWishlist(Long productId, String userId);

    /**
     * Get user's wishlist with pagination
     */
    Page<WishlistResponse> getUserWishlist(String userId, Pageable pageable);

    /**
     * Get user's wishlist by priority
     */
    List<WishlistResponse> getUserWishlistByPriority(String userId, Integer priority);

    /**
     * Update wishlist item notes and priority
     */
    WishlistResponse updateWishlistItem(Long wishlistId, WishlistRequest request, String userId);

    /**
     * Check if product is in user's wishlist
     */
    boolean isProductInWishlist(Long productId, String userId);

    /**
     * Get wishlist count for user
     */
    long getWishlistCount(String userId);

    /**
     * Clear user's entire wishlist
     */
    void clearWishlist(String userId);

    /**
     * Get wishlist item by ID
     */
    WishlistResponse getWishlistItem(Long wishlistId, String userId);
}