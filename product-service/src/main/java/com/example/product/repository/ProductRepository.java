package com.example.product.repository;

import com.example.product.enums.ProductStatus;
import com.example.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search methods
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);

    Page<Product> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategoryNameContainingIgnoreCase(
            String name, String categoryName, Pageable pageable);

    // Seller methods
    Page<Product> findBySellerId(String sellerId, Pageable pageable);

    boolean existsByIdAndSellerId(Long id, String sellerId);

    // Statistics methods
    long countByStatus(ProductStatus status);

    long countByIsFeaturedTrue();

    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.category c GROUP BY c.name")
    List<Object[]> getProductCountByCategory();
}