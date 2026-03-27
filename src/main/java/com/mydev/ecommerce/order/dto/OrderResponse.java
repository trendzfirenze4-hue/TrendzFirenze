// package com.mydev.ecommerce.order.dto;

// import java.math.BigDecimal;
// import java.util.List;

// public record OrderResponse(
//         Long id,
//         String orderNumber,
//         String status,
//         String paymentMethod,
//         BigDecimal subtotalAmount,
//         BigDecimal shippingAmount,
//         BigDecimal totalAmount,
//         String addressFullName,
//         String addressPhone,
//         String addressLine1,
//         String addressLine2,
//         String addressCity,
//         String addressState,
//         String addressPincode,
//         String addressCountry,
//         List<OrderItemResponse> items
// ) {}





// package com.mydev.ecommerce.order.dto;

// import java.math.BigDecimal;
// import java.time.OffsetDateTime;
// import java.util.List;

// public record OrderResponse(
//         Long id,
//         String orderNumber,
//         String status,
//         String paymentMethod,
//         String paymentStatus,
//         BigDecimal subtotalAmount,
//         BigDecimal shippingAmount,
//         BigDecimal totalAmount,
//         String addressFullName,
//         String addressPhone,
//         String addressLine1,
//         String addressLine2,
//         String addressCity,
//         String addressState,
//         String addressPincode,
//         String addressCountry,
//         OffsetDateTime createdAt,
//         List<OrderItemResponse> items
// ) {}





package com.mydev.ecommerce.order.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        String paymentMethod,
        String paymentStatus,
        BigDecimal subtotalAmount,
        BigDecimal shippingAmount,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String couponCode,
        String addressFullName,
        String addressPhone,
        String addressLine1,
        String addressLine2,
        String addressCity,
        String addressState,
        String addressPincode,
        String addressCountry,
        OffsetDateTime createdAt,
        List<OrderItemResponse> items
) {
}