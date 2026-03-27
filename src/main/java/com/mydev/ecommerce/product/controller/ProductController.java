


package com.mydev.ecommerce.product.controller;

import com.mydev.ecommerce.product.dto.ProductResponse;
import com.mydev.ecommerce.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> list(
            @RequestParam(required = false) Long categoryId
    ) {
        return service.getProducts(categoryId);
    }

    @GetMapping("/{id}")
    public ProductResponse one(@PathVariable Long id) {
        return service.getProduct(id);
    }
}