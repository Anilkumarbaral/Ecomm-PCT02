package com.example.product.service.impl;

import com.example.product.dto.request.ProductRequest;
import com.example.product.dto.response.ProductResponse;
import com.example.product.dto.response.ProductStatisticsResponse;
import com.example.product.enums.ProductStatus;
import com.example.product.model.Category;
import com.example.product.model.Product;
import com.example.product.model.Wishlist;
import com.example.product.repository.CategoryRepository;
import com.example.product.repository.ProductRepository;
import com.example.product.repository.WishlistRepository;
import com.example.product.security.UserPrincipal;
import com.example.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request, UserPrincipal user) {
        log.info("Creating product: {} by user: {}", request.getName(), user.getUserId());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setComparePrice(request.getComparePrice());
        product.setBrand(request.getBrand());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setStatus(ProductStatus.valueOf(request.getStatus()));
        product.setIsFeatured(request.getIsFeatured());
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setSellerId(user.getUserId()); // Set the seller ID from authenticated user
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable, String category, String search) {
        Page<Product> products;

        if (StringUtils.hasText(search) && StringUtils.hasText(category)) {
            products = productRepository.findByNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
                    search, category, pageable);
        } else if (StringUtils.hasText(search)) {
            products = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable);
        } else if (StringUtils.hasText(category)) {
            products = productRepository.findByCategoryNameContainingIgnoreCase(category, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::mapToResponse);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, UserPrincipal user) {
        log.info("Updating product: {} by user: {}", id, user.getUserId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // Check if user is authorized to update this product (for sellers)
        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_SELLER"))) {
            if (!product.getSellerId().equals(user.getUserId())) {
                throw new SecurityException("You can only update your own products");
            }
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setShortDescription(request.getShortDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setComparePrice(request.getComparePrice());
        product.setBrand(request.getBrand());
        product.setWeight(request.getWeight());
        product.setDimensions(request.getDimensions());
        product.setStatus(ProductStatus.valueOf(request.getStatus()));
        product.setIsFeatured(request.getIsFeatured());
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setUpdatedAt(LocalDateTime.now());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getId());

        return mapToResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id, UserPrincipal user) {
        log.info("Deleting product: {} by user: {}", id, user.getUserId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        // Check if user is authorized to delete this product (for sellers)
        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_SELLER"))) {
            if (!product.getSellerId().equals(user.getUserId())) {
                throw new SecurityException("You can only delete your own products");
            }
        }

        // Remove from all wishlists first
        wishlistRepository.deleteByProductId(id);

        productRepository.deleteById(id);
        log.info("Product deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySeller(String sellerId, Pageable pageable) {
        Page<Product> products = productRepository.findBySellerId(sellerId, pageable);
        return products.map(this::mapToResponse);
    }

    @Override
    public void addToWishlist(Long productId, String userId) {
        log.info("Adding product {} to wishlist for user: {}", productId, userId);

        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new IllegalStateException("Product is already in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProduct(product);
        wishlist.setCreatedAt(LocalDateTime.now());

        wishlistRepository.save(wishlist);
        log.info("Product added to wishlist successfully");
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
    public ProductResponse updateProductStatus(Long id, boolean active, UserPrincipal user) {
        log.info("Admin {} changing product {} status to: {}", user.getUserId(), id, active);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        product.setStatus(active ? ProductStatus.ACTIVE : ProductStatus.INACTIVE);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Product status updated successfully");

        return mapToResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductStatisticsResponse getProductStatistics() {
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByStatus(ProductStatus.ACTIVE);
        long inactiveProducts = productRepository.countByStatus(ProductStatus.INACTIVE);
        long featuredProducts = productRepository.countByIsFeaturedTrue();

        // Get category statistics
        List<Object[]> categoryStats = productRepository.getProductCountByCategory();

        ProductStatisticsResponse stats = new ProductStatisticsResponse();
        stats.setTotalProducts(totalProducts);
        stats.setActiveProducts(activeProducts);
        stats.setInactiveProducts(inactiveProducts);
        stats.setFeaturedProducts(featuredProducts);
        stats.setCategoryStatistics(categoryStats);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProductOwner(Long productId, String userId) {
        return productRepository.existsByIdAndSellerId(productId, userId);
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse res = new ProductResponse();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());
        res.setSku(product.getSku());
        res.setPrice(product.getPrice());
        res.setComparePrice(product.getComparePrice());
        res.setBrand(product.getBrand());
        res.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
//res.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        res.setWeight(product.getWeight());
        res.setDimensions(product.getDimensions());
        res.setShortDescription(product.getShortDescription());
        res.setStatus(product.getStatus().name());
        res.setIsFeatured(product.getIsFeatured());
        res.setMetaTitle(product.getMetaTitle());
        res.setMetaDescription(product.getMetaDescription());
      //  res.setSellerId(product.getSellerId());
        res.setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().toString() : null);
        res.setUpdatedAt(product.getUpdatedAt() != null ? product.getUpdatedAt().toString() : null);
        return res;
    }
}