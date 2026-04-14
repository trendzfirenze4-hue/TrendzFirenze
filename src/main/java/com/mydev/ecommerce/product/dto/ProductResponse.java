




package com.mydev.ecommerce.product.dto;

import java.util.List;

public record ProductResponse(
        Long id,
        String title,
        String description,
        Integer priceInr,
        Integer mrpInr,
        Integer discountPercent,
        Integer stock,
        String category,
        List<String> images,
        List<ProductReviewResponse> reviews
) {}