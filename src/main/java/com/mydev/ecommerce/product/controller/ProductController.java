package com.mydev.ecommerce.product.controller;

import com.mydev.ecommerce.product.model.Product;
import com.mydev.ecommerce.product.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductRepository repo;

  public ProductController(ProductRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Product> list(@RequestParam(required = false) Long categoryId) {
    if (categoryId != null) return repo.findByCategory_Id(categoryId);
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public Product one(@PathVariable Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
  }
}