package com.mydev.ecommerce.product.repository;

import com.mydev.ecommerce.product.model.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    List<ProductReview> findByProductIdOrderByIdDesc(Long productId);

    Optional<ProductReview> findByIdAndProductId(Long id, Long productId);
}