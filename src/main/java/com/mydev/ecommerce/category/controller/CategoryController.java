package com.mydev.ecommerce.category.controller;

import com.mydev.ecommerce.category.model.Category;
import com.mydev.ecommerce.category.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryRepository repo;

  public CategoryController(CategoryRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<Category> list() {
    return repo.findAll();
  }
}