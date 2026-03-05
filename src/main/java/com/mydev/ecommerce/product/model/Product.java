package com.mydev.ecommerce.product.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.mydev.ecommerce.category.model.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name="products")
@Getter @Setter
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, length=200)
  private String title;

  @Column(columnDefinition="text")
  private String description;

  @Column(name="price_inr", nullable=false)
  private Integer priceInr;

  @Column(nullable=false)
  private Integer stock;

  // @ManyToOne(fetch = FetchType.LAZY)
  // @JoinColumn(name="category_id")
  // private Category category;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="category_id")
  private Category category;

  // keep simple in MVP: store JSON as String
  // @Column(columnDefinition="jsonb", nullable=false)
  // private String images;

  @JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "jsonb", nullable = false)
private String images;

  @Column(name="created_at", nullable=false)
  private OffsetDateTime createdAt;
}