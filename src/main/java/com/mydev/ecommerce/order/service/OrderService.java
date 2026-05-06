

// package com.mydev.ecommerce.order.service;

// import com.mydev.ecommerce.address.model.Address;
// import com.mydev.ecommerce.address.repository.AddressRepository;
// import com.mydev.ecommerce.cart.model.Cart;
// import com.mydev.ecommerce.cart.model.CartItem;
// import com.mydev.ecommerce.cart.repository.CartRepository;
// import com.mydev.ecommerce.coupon.dto.CouponCalculationResult;
// import com.mydev.ecommerce.coupon.service.CouponService;
// import com.mydev.ecommerce.email.dto.OrderEmailPayload;
// import com.mydev.ecommerce.email.service.OrderEmailService;

// import com.mydev.ecommerce.whatsapp.service.WhatsAppService;

// import com.mydev.ecommerce.order.dto.OrderItemResponse;
// import com.mydev.ecommerce.order.dto.OrderResponse;
// import com.mydev.ecommerce.order.dto.PlaceOrderRequest;
// import com.mydev.ecommerce.order.dto.UpdateOrderStatusRequest;
// import com.mydev.ecommerce.order.model.Order;
// import com.mydev.ecommerce.order.model.OrderItem;
// import com.mydev.ecommerce.order.model.OrderStatus;
// import com.mydev.ecommerce.order.model.PaymentMethod;
// import com.mydev.ecommerce.order.repository.OrderRepository;
// import com.mydev.ecommerce.user.model.User;
// import com.mydev.ecommerce.user.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.math.BigDecimal;
// import java.util.List;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class OrderService {

//     private final UserRepository userRepository;
//     private final AddressRepository addressRepository;
//     private final CartRepository cartRepository;
//     private final OrderRepository orderRepository;
//     private final CouponService couponService;
//     private final OrderEmailService orderEmailService;
//     private final WhatsAppService whatsAppService;

//     public OrderResponse placeOrder(Authentication authentication, PlaceOrderRequest request) {
//         User user = getUser(authentication);

//         Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
//                 .orElseThrow(() -> new RuntimeException("Address not found"));

//         Cart cart = cartRepository.findByUserId(user.getId())
//                 .orElseThrow(() -> new RuntimeException("Cart not found"));

//         if (cart.getItems() == null || cart.getItems().isEmpty()) {
//             throw new RuntimeException("Cart is empty");
//         }

//         BigDecimal subtotal = BigDecimal.ZERO;

//         Order order = new Order();
//         order.setOrderNumber(generateOrderNumber());
//         order.setUser(user);
//         order.setPaymentMethod(PaymentMethod.valueOf(request.paymentMethod().toUpperCase()));
//         order.setStatus(OrderStatus.PLACED);

//         setAddress(order, address);

//         for (CartItem cartItem : cart.getItems()) {
//             if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
//                 throw new RuntimeException("Insufficient stock for product: " + cartItem.getProduct().getTitle());
//             }

//             OrderItem orderItem = buildOrderItem(order, cartItem);
//             order.getItems().add(orderItem);
//             subtotal = subtotal.add(orderItem.getLineTotal());

//             cartItem.getProduct().setStock(
//                     cartItem.getProduct().getStock() - cartItem.getQuantity()
//             );
//         }

//         BigDecimal shipping = BigDecimal.ZERO;
//         BigDecimal discount = BigDecimal.ZERO;
//         BigDecimal total = subtotal;

//         boolean hasCoupon = request.couponCode() != null && !request.couponCode().isBlank();
//         CouponCalculationResult couponResult = null;

//         if (hasCoupon) {
//             couponResult = couponService.validateAndCalculate(request.couponCode(), subtotal);
//             discount = couponResult.discountAmount();
//             total = subtotal.subtract(discount);
//         }

//         order.setSubtotalAmount(subtotal);
//         order.setShippingAmount(shipping);
//         order.setDiscountAmount(discount);
//         order.setTotalAmount(total);
//         order.setCouponCode(hasCoupon ? couponResult.coupon().getCode() : null);

//         Order saved = orderRepository.save(order);

//         if (hasCoupon) {
//             couponService.consumeCoupon(couponResult.coupon(), user, saved);
//         }

//         cart.getItems().clear();
//         cartRepository.save(cart);

//         sendOrderEmails(user, saved, "COD");
//         whatsAppService.sendOrderPlacedMessage(saved);

//         return map(saved);
//         }

//     @Transactional(readOnly = true)
//     public List<OrderResponse> adminAllOrders() {
//         return orderRepository.findAllByOrderByIdDesc()
//                 .stream()
//                 .map(this::map)
//                 .toList();
//     }

//     public OrderResponse adminUpdateStatus(Long orderId, UpdateOrderStatusRequest request) {
//         Order order = orderRepository.findById(orderId)
//                 .orElseThrow(() -> new RuntimeException("Order not found"));

//         order.setStatus(OrderStatus.valueOf(request.status().toUpperCase()));

//         Order saved = orderRepository.save(order);
//         return map(saved);
//     }

//     @Transactional(readOnly = true)
//     public List<OrderResponse> myOrders(Authentication authentication) {
//         User user = getUser(authentication);

//         return orderRepository.findByUserIdOrderByIdDesc(user.getId())
//                 .stream()
//                 .map(this::map)
//                 .toList();
//     }

//     @Transactional(readOnly = true)
//     public OrderResponse myOrderById(Authentication authentication, Long orderId) {
//         User user = getUser(authentication);

//         Order order = orderRepository.findByIdAndUserId(orderId, user.getId())
//                 .orElseThrow(() -> new RuntimeException("Order not found"));

//         return map(order);
//     }

//     private void sendOrderEmails(User user, Order order, String type) {
//         OrderEmailPayload payload = buildEmailPayload(user, order);

//         orderEmailService.sendCodOrderPlacedCustomerEmail(payload);
//         orderEmailService.sendOrderAdminNotification(
//                 payload,
//                 "COD".equalsIgnoreCase(type) ? "New COD order placed." : "New order placed."
//         );
//     }

//     private OrderEmailPayload buildEmailPayload(User user, Order order) {
//         return OrderEmailPayload.builder()
//                 .customerName(user.getName())
//                 .customerEmail(user.getEmail())
//                 .orderNumber(order.getOrderNumber())
//                 .orderStatus(order.getStatus().name())
//                 .paymentMethod(order.getPaymentMethod().name())
//                 .paymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "PENDING")
//                 .subtotalAmount(order.getSubtotalAmount())
//                 .shippingAmount(order.getShippingAmount())
//                 .discountAmount(order.getDiscountAmount())
//                 .totalAmount(order.getTotalAmount())
//                 .couponCode(order.getCouponCode())
//                 .addressFullName(order.getAddressFullName())
//                 .addressPhone(order.getAddressPhone())
//                 .addressLine1(order.getAddressLine1())
//                 .addressLine2(order.getAddressLine2())
//                 .addressCity(order.getAddressCity())
//                 .addressState(order.getAddressState())
//                 .addressPincode(order.getAddressPincode())
//                 .addressCountry(order.getAddressCountry())
//                 .createdAt(order.getCreatedAt())
//                 .items(order.getItems().stream()
//                         .map(item -> OrderEmailPayload.OrderEmailItemPayload.builder()
//                                 .productTitle(item.getProductTitle())
//                                 .quantity(item.getQuantity())
//                                 .unitPrice(item.getUnitPrice())
//                                 .lineTotal(item.getLineTotal())
//                                 .imageUrl(item.getImageUrl())
//                                 .build())
//                         .toList())
//                 .build();
//     }

//     private void setAddress(Order order, Address address) {
//         order.setAddressFullName(address.getFullName());
//         order.setAddressPhone(address.getPhone());
//         order.setAddressLine1(address.getLine1());
//         order.setAddressLine2(address.getLine2());
//         order.setAddressCity(address.getCity());
//         order.setAddressState(address.getState());
//         order.setAddressPincode(address.getPincode());
//         order.setAddressCountry(address.getCountry());
//     }

//     private OrderItem buildOrderItem(Order order, CartItem cartItem) {
//         OrderItem item = new OrderItem();
//         item.setOrder(order);
//         item.setProduct(cartItem.getProduct());
//         item.setProductTitle(cartItem.getProduct().getTitle());
//         item.setQuantity(cartItem.getQuantity());

//         BigDecimal unitPrice = cartItem.getUnitPriceSnapshot();
//         BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

//         item.setUnitPrice(unitPrice);
//         item.setLineTotal(lineTotal);

//         if (cartItem.getProduct().getImages() != null &&
//                 !cartItem.getProduct().getImages().isEmpty()) {
//             item.setImageUrl(cartItem.getProduct().getImages().get(0).getImageUrl());
//         }

//         return item;
//     }

//     private String generateOrderNumber() {
//         return "TF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//     }

//     private User getUser(Authentication authentication) {
//         return userRepository.findByEmail(authentication.getName())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//     }

//     private OrderResponse map(Order order) {
//         List<OrderItemResponse> items = order.getItems().stream()
//                 .map(item -> new OrderItemResponse(
//                         item.getId(),
//                         item.getProduct().getId(),
//                         item.getProductTitle(),
//                         item.getImageUrl(),
//                         item.getQuantity(),
//                         item.getUnitPrice(),
//                         item.getLineTotal()
//                 ))
//                 .toList();

//         return new OrderResponse(
//                 order.getId(),
//                 order.getOrderNumber(),
//                 order.getStatus().name(),
//                 order.getPaymentMethod().name(),
//                 order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "PENDING",
//                 order.getSubtotalAmount(),
//                 order.getShippingAmount(),
//                 order.getDiscountAmount(),
//                 order.getTotalAmount(),
//                 order.getCouponCode(),
//                 order.getAddressFullName(),
//                 order.getAddressPhone(),
//                 order.getAddressLine1(),
//                 order.getAddressLine2(),
//                 order.getAddressCity(),
//                 order.getAddressState(),
//                 order.getAddressPincode(),
//                 order.getAddressCountry(),
//                 order.getCreatedAt(),
//                 items
//         );
//     }
// }


























package com.mydev.ecommerce.order.service;

import com.mydev.ecommerce.address.model.Address;
import com.mydev.ecommerce.address.repository.AddressRepository;
import com.mydev.ecommerce.cart.model.Cart;
import com.mydev.ecommerce.cart.model.CartItem;
import com.mydev.ecommerce.cart.repository.CartRepository;
import com.mydev.ecommerce.coupon.dto.CouponCalculationResult;
import com.mydev.ecommerce.coupon.service.CouponService;
import com.mydev.ecommerce.email.dto.OrderEmailPayload;
import com.mydev.ecommerce.email.service.OrderEmailService;
import com.mydev.ecommerce.whatsapp.service.WhatsAppService;
import com.mydev.ecommerce.order.dto.OrderItemResponse;
import com.mydev.ecommerce.order.dto.OrderResponse;
import com.mydev.ecommerce.order.dto.PlaceOrderRequest;
import com.mydev.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.mydev.ecommerce.order.model.Order;
import com.mydev.ecommerce.order.model.OrderItem;
import com.mydev.ecommerce.order.model.OrderStatus;
import com.mydev.ecommerce.order.model.PaymentMethod;
import com.mydev.ecommerce.order.repository.OrderRepository;
import com.mydev.ecommerce.user.model.User;
import com.mydev.ecommerce.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    private final CouponService couponService;

    private final OrderEmailService orderEmailService;

    private final WhatsAppService whatsAppService;

    /* =========================================
       PLACE ORDER
    ========================================= */

    public OrderResponse placeOrder(
            Authentication authentication,
            PlaceOrderRequest request
    ) {

        User user = getUser(authentication);

        Address address =
                addressRepository
                        .findByIdAndUserId(
                                request.addressId(),
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Address not found"
                                )
                        );

        Cart cart =
                cartRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Cart not found"
                                )
                        );

        if (
                cart.getItems() == null ||
                cart.getItems().isEmpty()
        ) {
            throw new RuntimeException(
                    "Cart is empty"
            );
        }

        BigDecimal subtotal =
                BigDecimal.ZERO;

        Order order = new Order();

        order.setOrderNumber(
                generateOrderNumber()
        );

        order.setUser(user);

        order.setPaymentMethod(
                PaymentMethod.valueOf(
                        request.paymentMethod()
                                .toUpperCase()
                )
        );

        order.setStatus(
                OrderStatus.PLACED
        );

        setAddress(order, address);

        /* =========================================
           ORDER ITEMS
        ========================================= */

        for (CartItem cartItem : cart.getItems()) {

            if (
                    cartItem.getProduct()
                            .getStock() <
                            cartItem.getQuantity()
            ) {

                throw new RuntimeException(
                        "Insufficient stock for product: " +
                                cartItem.getProduct()
                                        .getTitle()
                );
            }

            OrderItem orderItem =
                    buildOrderItem(
                            order,
                            cartItem
                    );

            order.getItems().add(orderItem);

            subtotal =
                    subtotal.add(
                            orderItem.getLineTotal()
                    );

            cartItem.getProduct().setStock(
                    cartItem.getProduct()
                            .getStock() -
                            cartItem.getQuantity()
            );
        }

        /* =========================================
           COUPON
        ========================================= */

        BigDecimal shipping =
                BigDecimal.ZERO;

        BigDecimal discount =
                BigDecimal.ZERO;

        BigDecimal total =
                subtotal;

        boolean hasCoupon =
                request.couponCode() != null &&
                        !request.couponCode()
                                .isBlank();

        CouponCalculationResult couponResult =
                null;

        if (hasCoupon) {

            couponResult =
                    couponService
                            .validateAndCalculate(
                                    request.couponCode(),
                                    subtotal
                            );

            discount =
                    couponResult.discountAmount();

            total =
                    subtotal.subtract(discount);
        }

        order.setSubtotalAmount(subtotal);

        order.setShippingAmount(shipping);

        order.setDiscountAmount(discount);

        order.setTotalAmount(total);

        order.setCouponCode(
                hasCoupon
                        ? couponResult
                        .coupon()
                        .getCode()
                        : null
        );

        /* =========================================
           SAVE ORDER
        ========================================= */

        Order saved =
                orderRepository.save(order);

        if (hasCoupon) {

            couponService.consumeCoupon(
                    couponResult.coupon(),
                    user,
                    saved
            );
        }

        /* =========================================
           CLEAR CART
        ========================================= */

        cart.getItems().clear();

        cartRepository.save(cart);

        /* =========================================
           EMAIL SAFETY
        ========================================= */

        try {

            sendOrderEmails(
                    user,
                    saved,
                    "COD"
            );

        } catch (Exception e) {

            log.error(
                    "ORDER EMAIL FAILED -> order={}, reason={}",
                    saved.getOrderNumber(),
                    e.getMessage(),
                    e
            );
        }

        /* =========================================
           WHATSAPP SAFETY
        ========================================= */

        try {

            whatsAppService
                    .sendOrderPlacedMessage(saved);

        } catch (Exception e) {

            log.error(
                    "WHATSAPP MESSAGE FAILED -> order={}, reason={}",
                    saved.getOrderNumber(),
                    e.getMessage(),
                    e
            );
        }

        return map(saved);
    }

    /* =========================================
       ADMIN ORDERS
    ========================================= */

    @Transactional(readOnly = true)
    public List<OrderResponse> adminAllOrders() {

        return orderRepository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::map)
                .toList();
    }

    /* =========================================
       UPDATE ORDER STATUS
    ========================================= */

    public OrderResponse adminUpdateStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {

        Order order =
                orderRepository.findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        order.setStatus(
                OrderStatus.valueOf(
                        request.status()
                                .toUpperCase()
                )
        );

        Order saved =
                orderRepository.save(order);

        return map(saved);
    }

    /* =========================================
       MY ORDERS
    ========================================= */

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(
            Authentication authentication
    ) {

        User user = getUser(authentication);

        return orderRepository
                .findByUserIdOrderByIdDesc(
                        user.getId()
                )
                .stream()
                .map(this::map)
                .toList();
    }

    /* =========================================
       ORDER BY ID
    ========================================= */

    @Transactional(readOnly = true)
    public OrderResponse myOrderById(
            Authentication authentication,
            Long orderId
    ) {

        User user = getUser(authentication);

        Order order =
                orderRepository
                        .findByIdAndUserId(
                                orderId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        return map(order);
    }

    /* =========================================
       SEND EMAILS
    ========================================= */

    private void sendOrderEmails(
            User user,
            Order order,
            String type
    ) {

        OrderEmailPayload payload =
                buildEmailPayload(
                        user,
                        order
                );

        orderEmailService
                .sendCodOrderPlacedCustomerEmail(
                        payload
                );

        orderEmailService
                .sendOrderAdminNotification(
                        payload,
                        "COD".equalsIgnoreCase(type)
                                ? "New COD order placed."
                                : "New order placed."
                );
    }

    /* =========================================
       BUILD EMAIL PAYLOAD
    ========================================= */

    private OrderEmailPayload buildEmailPayload(
            User user,
            Order order
    ) {

        return OrderEmailPayload.builder()

                .customerName(
                        user.getName()
                )

                .customerEmail(
                        user.getEmail()
                )

                .orderNumber(
                        order.getOrderNumber()
                )

                .orderStatus(
                        order.getStatus().name()
                )

                .paymentMethod(
                        order.getPaymentMethod().name()
                )

                .paymentStatus(
                        order.getPaymentStatus() != null
                                ? order.getPaymentStatus().name()
                                : "PENDING"
                )

                .subtotalAmount(
                        order.getSubtotalAmount()
                )

                .shippingAmount(
                        order.getShippingAmount()
                )

                .discountAmount(
                        order.getDiscountAmount()
                )

                .totalAmount(
                        order.getTotalAmount()
                )

                .couponCode(
                        order.getCouponCode()
                )

                .addressFullName(
                        order.getAddressFullName()
                )

                .addressPhone(
                        order.getAddressPhone()
                )

                .addressLine1(
                        order.getAddressLine1()
                )

                .addressLine2(
                        order.getAddressLine2()
                )

                .addressCity(
                        order.getAddressCity()
                )

                .addressState(
                        order.getAddressState()
                )

                .addressPincode(
                        order.getAddressPincode()
                )

                .addressCountry(
                        order.getAddressCountry()
                )

                .createdAt(
                        order.getCreatedAt()
                )

                .items(
                        order.getItems()
                                .stream()
                                .map(item ->
                                        OrderEmailPayload
                                                .OrderEmailItemPayload
                                                .builder()

                                                .productTitle(
                                                        item.getProductTitle()
                                                )

                                                .quantity(
                                                        item.getQuantity()
                                                )

                                                .unitPrice(
                                                        item.getUnitPrice()
                                                )

                                                .lineTotal(
                                                        item.getLineTotal()
                                                )

                                                .imageUrl(
                                                        item.getImageUrl()
                                                )

                                                .build()
                                )
                                .toList()
                )

                .build();
    }

    /* =========================================
       ADDRESS
    ========================================= */

    private void setAddress(
            Order order,
            Address address
    ) {

        order.setAddressFullName(
                address.getFullName()
        );

        order.setAddressPhone(
                address.getPhone()
        );

        order.setAddressLine1(
                address.getLine1()
        );

        order.setAddressLine2(
                address.getLine2()
        );

        order.setAddressCity(
                address.getCity()
        );

        order.setAddressState(
                address.getState()
        );

        order.setAddressPincode(
                address.getPincode()
        );

        order.setAddressCountry(
                address.getCountry()
        );
    }

    /* =========================================
       BUILD ORDER ITEM
    ========================================= */

    private OrderItem buildOrderItem(
            Order order,
            CartItem cartItem
    ) {

        OrderItem item =
                new OrderItem();

        item.setOrder(order);

        item.setProduct(
                cartItem.getProduct()
        );

        item.setProductTitle(
                cartItem.getProduct()
                        .getTitle()
        );

        item.setQuantity(
                cartItem.getQuantity()
        );

        BigDecimal unitPrice =
                cartItem.getUnitPriceSnapshot();

        BigDecimal lineTotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                cartItem.getQuantity()
                        )
                );

        item.setUnitPrice(unitPrice);

        item.setLineTotal(lineTotal);

        if (
                cartItem.getProduct()
                        .getImages() != null &&
                        !cartItem.getProduct()
                                .getImages()
                                .isEmpty()
        ) {

            item.setImageUrl(
                    cartItem.getProduct()
                            .getImages()
                            .get(0)
                            .getImageUrl()
            );
        }

        return item;
    }

    /* =========================================
       ORDER NUMBER
    ========================================= */

    private String generateOrderNumber() {

        return "TF-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    /* =========================================
       GET USER
    ========================================= */

    private User getUser(
            Authentication authentication
    ) {

        return userRepository
                .findByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }

    /* =========================================
       MAP RESPONSE
    ========================================= */

    private OrderResponse map(
            Order order
    ) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(item ->
                                new OrderItemResponse(
                                        item.getId(),
                                        item.getProduct().getId(),
                                        item.getProductTitle(),
                                        item.getImageUrl(),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        item.getLineTotal()
                                )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.getPaymentStatus() != null
                        ? order.getPaymentStatus().name()
                        : "PENDING",
                order.getSubtotalAmount(),
                order.getShippingAmount(),
                order.getDiscountAmount(),
                order.getTotalAmount(),
                order.getCouponCode(),
                order.getAddressFullName(),
                order.getAddressPhone(),
                order.getAddressLine1(),
                order.getAddressLine2(),
                order.getAddressCity(),
                order.getAddressState(),
                order.getAddressPincode(),
                order.getAddressCountry(),
                order.getCreatedAt(),
                items
        );
    }
}