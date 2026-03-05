package com.mydev.ecommerce.product.repository;

import com.mydev.ecommerce.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByCategory_Id(Long categoryId);
}