package com.mydev.ecommerce.product.controller;

import com.mydev.ecommerce.category.repository.CategoryRepository;
import com.mydev.ecommerce.product.dto.ProductRequest;
import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

  private final ProductRepository productRepo;
  private final CategoryRepository categoryRepo;

  public AdminProductController(ProductRepository productRepo, CategoryRepository categoryRepo) {
    this.productRepo = productRepo;
    this.categoryRepo = categoryRepo;
  }

  @PostMapping
  public Product create(@Valid @RequestBody ProductRequest req) {
    var cat = categoryRepo.findById(req.categoryId())
        .orElseThrow(() -> new RuntimeException("Category not found"));

    Product p = new Product();
    p.setTitle(req.title());
    p.setDescription(req.description());
    p.setPriceInr(req.priceInr());
    p.setStock(req.stock());
    p.setCategory(cat);
    p.setImages(req.imagesJson());
    p.setCreatedAt(OffsetDateTime.now());

    return productRepo.save(p);
  }
}