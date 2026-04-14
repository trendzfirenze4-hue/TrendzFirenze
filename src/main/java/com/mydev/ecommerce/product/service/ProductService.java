




package com.mydev.ecommerce.product.service;

import com.mydev.ecommerce.product.dto.ProductResponse;
import com.mydev.ecommerce.product.dto.ProductReviewResponse;
import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<ProductResponse> getProducts(Long categoryId) {

        List<Product> products;

        if (categoryId != null) {
            products = repo.findByCategoryIdWithImages(categoryId);
        } else {
            products = repo.findAllWithImages();
        }

        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProductResponse getProduct(Long id) {
        Product p = repo.findByIdWithImages(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        return mapToDTO(p);
    }

    private ProductResponse mapToDTO(Product p) {

        List<String> images = p.getImages()
                .stream()
                .map(i -> i.getImageUrl())
                .collect(Collectors.toList());

        List<ProductReviewResponse> reviews = p.getReviews()
                .stream()
                .map(r -> new ProductReviewResponse(
                        r.getId(),
                        r.getReviewerName(),
                        r.getRating(),
                        r.getReviewText(),
                        r.isFeatured()
                ))
                .toList();

        return new ProductResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getPriceInr(),
                p.getMrpInr(),
                p.getDiscountPercent(),
                p.getStock(),
                p.getCategory().getName(),
                images,
                reviews
        );
    }
}