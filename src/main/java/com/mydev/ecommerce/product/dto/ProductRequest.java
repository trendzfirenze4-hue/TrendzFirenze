

package com.mydev.ecommerce.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ProductRequest(

    @NotBlank
    @Size(max = 200)
    String title,

    String description,

    @NotNull
    Integer priceInr,

    Integer mrpInr,

    @NotNull
    Integer stock,

    @NotNull
    Long categoryId,

    List<String> images

) {}