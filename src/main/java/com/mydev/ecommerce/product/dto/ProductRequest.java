package com.mydev.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
    @NotBlank @Size(max=200) String title,
    String description,
    @NotNull Integer priceInr,
    @NotNull Integer stock,
    @NotNull Long categoryId,
    @NotNull String imagesJson // example: ["url1","url2"]
) {}