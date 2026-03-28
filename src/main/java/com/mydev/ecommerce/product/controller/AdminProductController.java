



package com.mydev.ecommerce.product.controller;

import com.mydev.ecommerce.category.repository.CategoryRepository;
import com.mydev.ecommerce.product.dto.ProductRequest;
import com.mydev.ecommerce.product.dto.ProductReviewRequest;
import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.model.ProductImage;
import com.mydev.ecommerce.product.model.ProductReview;
import com.mydev.ecommerce.product.repository.ProductRepository;
import com.mydev.ecommerce.product.repository.ProductReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductReviewRepository reviewRepo;

    public AdminProductController(ProductRepository productRepo,
                                  CategoryRepository categoryRepo,
                                  ProductReviewRepository reviewRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.reviewRepo = reviewRepo;
    }

    @GetMapping
    public List<Product> list() {
        return productRepo.findAllAdminWithImages();
    }

    @GetMapping("/{id}")
    public Product one(@PathVariable Long id) {
        return productRepo.findAdminByIdWithImages(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    @PostMapping
    @Transactional
    public Product create(@Valid @RequestBody ProductRequest req) {
        var cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product p = new Product();
        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setPriceInr(req.priceInr());
        p.setStock(req.stock());
        p.setCategory(cat);
        p.setCreatedAt(OffsetDateTime.now());
        p.setActive(true);
        p.setDeleted(false);

        if (req.images() != null) {
            for (String url : req.images()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(p);
                p.getImages().add(img);
            }
        }

        return productRepo.save(p);
    }

    @PutMapping("/{id}")
    @Transactional
    public Product update(@PathVariable Long id,
                          @Valid @RequestBody ProductRequest req) {

        Product p = productRepo.findAdminByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        var cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setPriceInr(req.priceInr());
        p.setStock(req.stock());
        p.setCategory(cat);

        p.getImages().clear();

        if (req.images() != null) {
            for (String url : req.images()) {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(p);
                p.getImages().add(img);
            }
        }

        return productRepo.saveAndFlush(p);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        p.setActive(false);
        p.setDeleted(true);
        productRepo.save(p);
    }

    @PostMapping("/{id}/reviews")
    @Transactional
    public ProductReview addReview(@PathVariable Long id,
                                   @Valid @RequestBody ProductReviewRequest req) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setReviewerName(req.reviewerName());
        review.setRating(req.rating());
        review.setReviewText(req.reviewText());
        review.setFeatured(Boolean.TRUE.equals(req.featured()));

        return reviewRepo.save(review);
    }

    @PutMapping("/{productId}/reviews/{reviewId}")
    @Transactional
    public ProductReview updateReview(@PathVariable Long productId,
                                      @PathVariable Long reviewId,
                                      @Valid @RequestBody ProductReviewRequest req) {
        ProductReview review = reviewRepo.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        review.setReviewerName(req.reviewerName());
        review.setRating(req.rating());
        review.setReviewText(req.reviewText());
        review.setFeatured(Boolean.TRUE.equals(req.featured()));

        return reviewRepo.save(review);
    }

    @DeleteMapping("/{productId}/reviews/{reviewId}")
    @Transactional
    public void deleteReview(@PathVariable Long productId,
                             @PathVariable Long reviewId) {
        ProductReview review = reviewRepo.findByIdAndProductId(reviewId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        reviewRepo.delete(review);
    }
}