




package com.mydev.ecommerce.product.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mydev.ecommerce.category.model.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "price_inr", nullable = false)
    private Integer priceInr;

    @Column(name = "mrp_inr")
    private Integer mrpInr;

    @Column(nullable = false)
    private Integer stock = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ProductImage> images = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    private Set<ProductReview> reviews = new LinkedHashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Transient
    public Integer getDiscountPercent() {
        if (mrpInr == null || mrpInr <= 0 || priceInr == null || priceInr >= mrpInr) {
            return 0;
        }
        return Math.round(((mrpInr - priceInr) * 100f) / mrpInr);
    }
}