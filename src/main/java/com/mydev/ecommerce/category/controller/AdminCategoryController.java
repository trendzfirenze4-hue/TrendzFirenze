// package com.mydev.ecommerce.category.controller;

// import com.mydev.ecommerce.category.dto.CategoryRequest;
// import com.mydev.ecommerce.category.model.Category;
// import com.mydev.ecommerce.category.repository.CategoryRepository;
// import jakarta.validation.Valid;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/admin/categories")
// public class AdminCategoryController {
//   private final CategoryRepository repo;

//   public AdminCategoryController(CategoryRepository repo) {
//     this.repo = repo;
//   }

//   @PostMapping
//   public Category create(@Valid @RequestBody CategoryRequest req) {
//     Category c = new Category();
//     c.setName(req.name());
//     return repo.save(c);
//   }

//   @DeleteMapping("/{id}")
//   public void delete(@PathVariable Long id) {
//     repo.deleteById(id);
//   }
// }





package com.mydev.ecommerce.category.controller;

import com.mydev.ecommerce.category.dto.CategoryRequest;
import com.mydev.ecommerce.category.model.Category;
import com.mydev.ecommerce.category.repository.CategoryRepository;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

  private final CategoryRepository repo;

  public AdminCategoryController(CategoryRepository repo) {
    this.repo = repo;
  }

  // CREATE
  @PostMapping
  public Category create(@Valid @RequestBody CategoryRequest req) {

    Category c = new Category();
    c.setName(req.name());

    return repo.save(c);
  }

  // UPDATE
  @PutMapping("/{id}")
  public Category update(
      @PathVariable Long id,
      @Valid @RequestBody CategoryRequest req) {

    Category c = repo.findById(id)
        .orElseThrow(() -> new RuntimeException("Category not found"));

    c.setName(req.name());

    return repo.save(c);
  }

  // DELETE
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    repo.deleteById(id);
  }
}