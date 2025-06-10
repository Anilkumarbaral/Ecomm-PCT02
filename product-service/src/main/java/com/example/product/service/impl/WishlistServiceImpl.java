package com.example.product.service.impl;


import com.example.product.dto.request.WishlistRequest;
import com.example.product.dto.response.WishlistResponse;
import com.example.product.model.Product;
import com.example.product.model.Wishlist;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.WishlistRepository;
import com.example.product.service.WishlistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Override
    public WishlistResponse addToWishlist(WishlistRequest request, String userId) {
        log.info("Adding product {} to wishlist for user: {}", request.getProductId(), userId);

        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new IllegalStateException("Product is already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProduct(product);
        wishlist.setNotes(request.getNotes());
        wishlist.setPriority(request.getPriority());
        wishlist.setCreatedAt(LocalDateTime.now());

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        log.info("Product added to wishlist successfully with id: {}", savedWishlist.getId());

        return mapToResponse(savedWishlist);
    }

    @Override
    public void removeFromWishlist(Long productId, String userId) {
        log.info("Removing product {} from wishlist for user: {}", productId, userId);

        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found in wishlist"));

        wishlistRepository.delete(wishlist);
        log.info("Product removed from wishlist successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistResponse> getUserWishlist(String userId, Pageable pageable) {
        log.debug("Fetching wishlist for user: {} with pagination", userId);

        Page<Wishlist> wishlistPage = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return wishlistPage.map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponse> getUserWishlistByPriority(String userId, Integer priority) {
        log.debug("Fetching wishlist for user: {} with priority: {}", userId, priority);

        List<Wishlist> wishlists = wishlistRepository.findByUserIdAndPriorityOrderByCreatedAtDesc(userId, priority);
        return wishlists.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WishlistResponse updateWishlistItem(Long wishlistId, WishlistRequest request, String userId) {
        log.info("Updating wishlist item {} for user: {}", wishlistId, userId);

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist item not found"));

        wishlist.setNotes(request.getNotes());
        wishlist.setPriority(request.getPriority());

        Wishlist updatedWishlist = wishlistRepository.save(wishlist);
        log.info("Wishlist item updated successfully");

        return mapToResponse(updatedWishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductInWishlist(Long productId, String userId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getWishlistCount(String userId) {
        return wishlistRepository.countByUserId(userId);
    }

    @Override
    public void clearWishlist(String userId) {
        log.info("Clearing wishlist for user: {}", userId);

        List<Wishlist> userWishlists = wishlistRepository.findByUserId(userId);
        if (!userWishlists.isEmpty()) {
            wishlistRepository.deleteAll(userWishlists);
            log.info("Cleared {} items from wishlist for user: {}", userWishlists.size(), userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WishlistResponse getWishlistItem(Long wishlistId, String userId) {
        log.debug("Fetching wishlist item {} for user: {}", wishlistId, userId);

        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist item not found"));

        return mapToResponse(wishlist);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlist.getId());
        response.setUserId(wishlist.getUserId());
        response.setProductId(wishlist.getProduct().getId());
        response.setProductName(wishlist.getProduct().getName());
        response.setProductSku(wishlist.getProduct().getSku());
     //   response.setProductPrice(wishlist.getProduct().getPrice());
        response.setProductBrand(wishlist.getProduct().getBrand());
        response.setProductStatus(wishlist.getProduct().getStatus().name());
        response.setCategoryName(wishlist.getProduct().getCategory() != null ?
                wishlist.getProduct().getCategory().getName() : null);
        response.setNotes(wishlist.getNotes());
        response.setPriority(wishlist.getPriority());
        response.setPriorityLabel(response.getPriorityLabel());
        response.setCreatedAt(wishlist.getCreatedAt().toString());

        return response;
    }
}
