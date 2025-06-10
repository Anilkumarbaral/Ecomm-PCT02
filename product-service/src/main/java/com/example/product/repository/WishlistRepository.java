package com.example.product.repository;


import com.example.product.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


import com.example.product.model.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUserIdAndProductId(String userId, Long productId);

    Optional<Wishlist> findByUserIdAndProductId(String userId, Long productId);

    Optional<Wishlist> findByIdAndUserId(Long id, String userId);

    Page<Wishlist> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<Wishlist> findByUserIdAndPriorityOrderByCreatedAtDesc(String userId, Integer priority);

    List<Wishlist> findByUserId(String userId);

    long countByUserId(String userId);

    void deleteByProductId(Long productId);
}