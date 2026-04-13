// package com.mydev.ecommerce.payment.service;

// import com.mydev.ecommerce.address.model.Address;
// import com.mydev.ecommerce.address.repository.AddressRepository;
// import com.mydev.ecommerce.cart.model.Cart;
// import com.mydev.ecommerce.cart.model.CartItem;
// import com.mydev.ecommerce.cart.repository.CartRepository;
// import com.mydev.ecommerce.order.model.Order;
// import com.mydev.ecommerce.order.model.OrderItem;
// import com.mydev.ecommerce.order.model.OrderStatus;
// import com.mydev.ecommerce.order.model.PaymentMethod;
// import com.mydev.ecommerce.order.model.PaymentStatus;
// import com.mydev.ecommerce.order.repository.OrderRepository;
// import com.mydev.ecommerce.payment.dto.CreateRazorpayOrderRequest;
// import com.mydev.ecommerce.payment.dto.CreateRazorpayOrderResponse;
// import com.mydev.ecommerce.payment.dto.VerifyRazorpayPaymentRequest;
// import com.mydev.ecommerce.payment.dto.VerifyRazorpayPaymentResponse;
// import com.mydev.ecommerce.user.model.User;
// import com.mydev.ecommerce.user.repository.UserRepository;
// import com.razorpay.RazorpayClient;
// import com.razorpay.Utils;
// import lombok.RequiredArgsConstructor;
// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.mydev.ecommerce.email.service.OrderEmailService;


// import java.math.BigDecimal;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// @Transactional
// public class PaymentService {

//     private final UserRepository userRepository;
//     private final AddressRepository addressRepository;
//     private final CartRepository cartRepository;
//     private final OrderRepository orderRepository;
//     private final OrderEmailService orderEmailService;

//     @Value("${razorpay.key-id}")
//     private String razorpayKeyId;

//     @Value("${razorpay.key-secret}")
//     private String razorpayKeySecret;

   
//     public CreateRazorpayOrderResponse createRazorpayOrder(
//         Authentication authentication,
//         CreateRazorpayOrderRequest request
// ) {
//     try {
//         System.out.println("========== CREATE RAZORPAY ORDER START ==========");
//         System.out.println("AUTH USER = " + authentication.getName());
//         System.out.println("ADDRESS ID = " + request.addressId());
//         System.out.println("RAZORPAY KEY ID = " + razorpayKeyId);
//         System.out.println("RAZORPAY KEY SECRET PRESENT = " + (razorpayKeySecret != null && !razorpayKeySecret.isBlank()));

//         User user = getUser(authentication);
//         System.out.println("USER ID = " + user.getId());
//         System.out.println("USER EMAIL = " + user.getEmail());

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
//         order.setPaymentMethod(PaymentMethod.ONLINE);
//         order.setPaymentStatus(PaymentStatus.PENDING);
//         order.setStatus(OrderStatus.PLACED);

//         order.setAddressFullName(address.getFullName());
//         order.setAddressPhone(address.getPhone());
//         order.setAddressLine1(address.getLine1());
//         order.setAddressLine2(address.getLine2());
//         order.setAddressCity(address.getCity());
//         order.setAddressState(address.getState());
//         order.setAddressPincode(address.getPincode());
//         order.setAddressCountry(address.getCountry());

//         for (CartItem cartItem : cart.getItems()) {
//             if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
//                 throw new RuntimeException("Insufficient stock for product: " + cartItem.getProduct().getTitle());
//             }

//             OrderItem orderItem = new OrderItem();
//             orderItem.setOrder(order);
//             orderItem.setProduct(cartItem.getProduct());
//             orderItem.setProductTitle(cartItem.getProduct().getTitle());
//             orderItem.setQuantity(cartItem.getQuantity());

//             BigDecimal unitPrice = cartItem.getUnitPriceSnapshot();
//             BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

//             orderItem.setUnitPrice(unitPrice);
//             orderItem.setLineTotal(lineTotal);

//             if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
//                 orderItem.setImageUrl(cartItem.getProduct().getImages().get(0).getImageUrl());
//             }

//             order.getItems().add(orderItem);
//             subtotal = subtotal.add(lineTotal);
//         }

//         BigDecimal shipping = BigDecimal.ZERO;
//         BigDecimal total = subtotal.add(shipping);

//         order.setSubtotalAmount(subtotal);
//         order.setShippingAmount(shipping);
//         order.setTotalAmount(total);

//         System.out.println("SUBTOTAL = " + subtotal);
//         System.out.println("TOTAL = " + total);

//         Order savedOrder = orderRepository.save(order);
//         System.out.println("DB ORDER ID = " + savedOrder.getId());
//         System.out.println("ORDER NUMBER = " + savedOrder.getOrderNumber());

//         RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

//         JSONObject options = new JSONObject();
//         options.put("amount", total.multiply(BigDecimal.valueOf(100)).intValue());
//         options.put("currency", "INR");
//         options.put("receipt", savedOrder.getOrderNumber());

//         System.out.println("RAZORPAY OPTIONS = " + options);

//         com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

//         System.out.println("RAZORPAY ORDER CREATED = " + razorpayOrder);

//         savedOrder.setRazorpayOrderId(razorpayOrder.get("id"));
//         orderRepository.save(savedOrder);

//         System.out.println("========== CREATE RAZORPAY ORDER SUCCESS ==========");

//         return new CreateRazorpayOrderResponse(
//                 savedOrder.getId(),
//                 savedOrder.getOrderNumber(),
//                 savedOrder.getRazorpayOrderId(),
//                 savedOrder.getTotalAmount().multiply(BigDecimal.valueOf(100)),
//                 "INR",
//                 razorpayKeyId
//         );

//     } catch (Exception e) {
//         System.out.println("========== CREATE RAZORPAY ORDER FAILED ==========");
//         e.printStackTrace();
//         throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
//     }
// }
































//     public VerifyRazorpayPaymentResponse verifyRazorpayPayment(
//             Authentication authentication,
//             VerifyRazorpayPaymentRequest request
//     ) {
//         try {
//             User user = getUser(authentication);

//             Order order = orderRepository.findByIdAndUserId(request.orderId(), user.getId())
//                     .orElseThrow(() -> new RuntimeException("Order not found"));

//             JSONObject options = new JSONObject();
//             options.put("razorpay_order_id", request.razorpayOrderId());
//             options.put("razorpay_payment_id", request.razorpayPaymentId());
//             options.put("razorpay_signature", request.razorpaySignature());

//             boolean valid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

//             if (!valid) {
//                 order.setPaymentStatus(PaymentStatus.FAILED);
//                 orderRepository.save(order);
//                 throw new RuntimeException("Invalid payment signature");
//             }

//             order.setRazorpayOrderId(request.razorpayOrderId());
//             order.setRazorpayPaymentId(request.razorpayPaymentId());
//             order.setRazorpaySignature(request.razorpaySignature());
//             order.setPaymentStatus(PaymentStatus.PAID);
//             order.setStatus(OrderStatus.CONFIRMED);

//             for (OrderItem item : order.getItems()) {
//                 if (item.getProduct().getStock() < item.getQuantity()) {
//                     throw new RuntimeException("Insufficient stock for product: " + item.getProductTitle());
//                 }
//             }

//             for (OrderItem item : order.getItems()) {
//                 item.getProduct().setStock(item.getProduct().getStock() - item.getQuantity());
//             }

//             orderRepository.save(order);

//             Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
//             if (cart != null && cart.getItems() != null) {
//                 cart.getItems().clear();
//                 cartRepository.save(cart);
//             }



//             OrderEmailPayload emailPayload = OrderEmailPayload.builder()
//         .customerName(user.getName())
//         .customerEmail(user.getEmail())
//         .orderNumber(order.getOrderNumber())
//         .orderStatus(order.getStatus().name())
//         .paymentMethod(order.getPaymentMethod().name())
//         .paymentStatus(order.getPaymentStatus().name())
//         .subtotalAmount(order.getSubtotalAmount())
//         .shippingAmount(order.getShippingAmount())
//         .discountAmount(order.getDiscountAmount())
//         .totalAmount(order.getTotalAmount())
//         .couponCode(order.getCouponCode())
//         .addressFullName(order.getAddressFullName())
//         .addressPhone(order.getAddressPhone())
//         .addressLine1(order.getAddressLine1())
//         .addressLine2(order.getAddressLine2())
//         .addressCity(order.getAddressCity())
//         .addressState(order.getAddressState())
//         .addressPincode(order.getAddressPincode())
//         .addressCountry(order.getAddressCountry())
//         .createdAt(order.getCreatedAt())
//         .items(order.getItems().stream()
//                 .map(item -> OrderEmailPayload.OrderEmailItemPayload.builder()
//                         .productTitle(item.getProductTitle())
//                         .quantity(item.getQuantity())
//                         .unitPrice(item.getUnitPrice())
//                         .lineTotal(item.getLineTotal())
//                         .imageUrl(item.getImageUrl())
//                         .build())
//                 .toList())
//         .build();

//         orderEmailService.sendPaidOrderConfirmedCustomerEmail(emailPayload);
//         orderEmailService.sendOrderAdminNotification(emailPayload, "New prepaid order confirmed.");







//             return new VerifyRazorpayPaymentResponse(
//                     "Payment successful. Your order has been confirmed.",
//                     order.getId(),
//                     order.getOrderNumber(),
//                     order.getPaymentStatus().name()
//             );

//         } catch (Exception e) {
//             throw new RuntimeException("Payment verification failed: " + e.getMessage(), e);
//         }
//     }

//     private User getUser(Authentication authentication) {
//         return userRepository.findByEmail(authentication.getName())
//                 .orElseThrow(() -> new RuntimeException("User not found"));
//     }

//     private String generateOrderNumber() {
//         return "TF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//     }
// }













package com.mydev.ecommerce.payment.service;

import com.mydev.ecommerce.address.model.Address;
import com.mydev.ecommerce.address.repository.AddressRepository;
import com.mydev.ecommerce.cart.model.Cart;
import com.mydev.ecommerce.cart.model.CartItem;
import com.mydev.ecommerce.cart.repository.CartRepository;
import com.mydev.ecommerce.email.dto.OrderEmailPayload;
import com.mydev.ecommerce.email.service.OrderEmailService;
import com.mydev.ecommerce.order.model.Order;
import com.mydev.ecommerce.order.model.OrderItem;
import com.mydev.ecommerce.order.model.OrderStatus;
import com.mydev.ecommerce.order.model.PaymentMethod;
import com.mydev.ecommerce.order.model.PaymentStatus;
import com.mydev.ecommerce.order.repository.OrderRepository;
import com.mydev.ecommerce.payment.dto.CreateRazorpayOrderRequest;
import com.mydev.ecommerce.payment.dto.CreateRazorpayOrderResponse;
import com.mydev.ecommerce.payment.dto.VerifyRazorpayPaymentRequest;
import com.mydev.ecommerce.payment.dto.VerifyRazorpayPaymentResponse;
import com.mydev.ecommerce.user.model.User;
import com.mydev.ecommerce.user.repository.UserRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderEmailService orderEmailService;

    @Value("${razorpay.key-id}")
    private String razorpayKeyId;

    @Value("${razorpay.key-secret}")
    private String razorpayKeySecret;

    public CreateRazorpayOrderResponse createRazorpayOrder(
            Authentication authentication,
            CreateRazorpayOrderRequest request
    ) {
        try {
            System.out.println("========== CREATE RAZORPAY ORDER START ==========");
            System.out.println("AUTH USER = " + authentication.getName());
            System.out.println("ADDRESS ID = " + request.addressId());
            System.out.println("RAZORPAY KEY ID = " + razorpayKeyId);
            System.out.println("RAZORPAY KEY SECRET PRESENT = " + (razorpayKeySecret != null && !razorpayKeySecret.isBlank()));

            User user = getUser(authentication);
            System.out.println("USER ID = " + user.getId());
            System.out.println("USER EMAIL = " + user.getEmail());

            Address address = addressRepository.findByIdAndUserId(request.addressId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));

            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            BigDecimal subtotal = BigDecimal.ZERO;

            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setUser(user);
            order.setPaymentMethod(PaymentMethod.ONLINE);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setStatus(OrderStatus.PLACED);

            order.setAddressFullName(address.getFullName());
            order.setAddressPhone(address.getPhone());
            order.setAddressLine1(address.getLine1());
            order.setAddressLine2(address.getLine2());
            order.setAddressCity(address.getCity());
            order.setAddressState(address.getState());
            order.setAddressPincode(address.getPincode());
            order.setAddressCountry(address.getCountry());

            for (CartItem cartItem : cart.getItems()) {
                if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + cartItem.getProduct().getTitle());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setProductTitle(cartItem.getProduct().getTitle());
                orderItem.setQuantity(cartItem.getQuantity());

                BigDecimal unitPrice = cartItem.getUnitPriceSnapshot();
                BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

                orderItem.setUnitPrice(unitPrice);
                orderItem.setLineTotal(lineTotal);

                if (cartItem.getProduct().getImages() != null && !cartItem.getProduct().getImages().isEmpty()) {
                    orderItem.setImageUrl(cartItem.getProduct().getImages().get(0).getImageUrl());
                }

                order.getItems().add(orderItem);
                subtotal = subtotal.add(lineTotal);
            }

            BigDecimal shipping = BigDecimal.ZERO;
            BigDecimal total = subtotal.add(shipping);

            order.setSubtotalAmount(subtotal);
            order.setShippingAmount(shipping);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setTotalAmount(total);

            System.out.println("SUBTOTAL = " + subtotal);
            System.out.println("TOTAL = " + total);

            Order savedOrder = orderRepository.save(order);
            System.out.println("DB ORDER ID = " + savedOrder.getId());
            System.out.println("ORDER NUMBER = " + savedOrder.getOrderNumber());

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject options = new JSONObject();
            options.put("amount", total.multiply(BigDecimal.valueOf(100)).intValue());
            options.put("currency", "INR");
            options.put("receipt", savedOrder.getOrderNumber());

            System.out.println("RAZORPAY OPTIONS = " + options);

            com.razorpay.Order razorpayOrder = razorpayClient.orders.create(options);

            System.out.println("RAZORPAY ORDER CREATED = " + razorpayOrder);

            savedOrder.setRazorpayOrderId(razorpayOrder.get("id"));
            orderRepository.save(savedOrder);

            System.out.println("========== CREATE RAZORPAY ORDER SUCCESS ==========");

            return new CreateRazorpayOrderResponse(
                    savedOrder.getId(),
                    savedOrder.getOrderNumber(),
                    savedOrder.getRazorpayOrderId(),
                    savedOrder.getTotalAmount().multiply(BigDecimal.valueOf(100)),
                    "INR",
                    razorpayKeyId
            );

        } catch (Exception e) {
            System.out.println("========== CREATE RAZORPAY ORDER FAILED ==========");
            e.printStackTrace();
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage(), e);
        }
    }

    public VerifyRazorpayPaymentResponse verifyRazorpayPayment(
            Authentication authentication,
            VerifyRazorpayPaymentRequest request
    ) {
        try {
            User user = getUser(authentication);

            Order order = orderRepository.findByIdAndUserId(request.orderId(), user.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.razorpayOrderId());
            options.put("razorpay_payment_id", request.razorpayPaymentId());
            options.put("razorpay_signature", request.razorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!valid) {
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                throw new RuntimeException("Invalid payment signature");
            }

            order.setRazorpayOrderId(request.razorpayOrderId());
            order.setRazorpayPaymentId(request.razorpayPaymentId());
            order.setRazorpaySignature(request.razorpaySignature());
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.CONFIRMED);

            for (OrderItem item : order.getItems()) {
                if (item.getProduct().getStock() < item.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for product: " + item.getProductTitle());
                }
            }

            for (OrderItem item : order.getItems()) {
                item.getProduct().setStock(item.getProduct().getStock() - item.getQuantity());
            }

            orderRepository.save(order);

            Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
            if (cart != null && cart.getItems() != null) {
                cart.getItems().clear();
                cartRepository.save(cart);
            }

            OrderEmailPayload emailPayload = OrderEmailPayload.builder()
                    .customerName(user.getName())
                    .customerEmail(user.getEmail())
                    .orderNumber(order.getOrderNumber())
                    .orderStatus(order.getStatus().name())
                    .paymentMethod(order.getPaymentMethod().name())
                    .paymentStatus(order.getPaymentStatus().name())
                    .subtotalAmount(order.getSubtotalAmount())
                    .shippingAmount(order.getShippingAmount())
                    .discountAmount(order.getDiscountAmount())
                    .totalAmount(order.getTotalAmount())
                    .couponCode(order.getCouponCode())
                    .addressFullName(order.getAddressFullName())
                    .addressPhone(order.getAddressPhone())
                    .addressLine1(order.getAddressLine1())
                    .addressLine2(order.getAddressLine2())
                    .addressCity(order.getAddressCity())
                    .addressState(order.getAddressState())
                    .addressPincode(order.getAddressPincode())
                    .addressCountry(order.getAddressCountry())
                    .createdAt(order.getCreatedAt())
                    .items(order.getItems().stream()
                            .map(item -> OrderEmailPayload.OrderEmailItemPayload.builder()
                                    .productTitle(item.getProductTitle())
                                    .quantity(item.getQuantity())
                                    .unitPrice(item.getUnitPrice())
                                    .lineTotal(item.getLineTotal())
                                    .imageUrl(item.getImageUrl())
                                    .build())
                            .toList())
                    .build();

            orderEmailService.sendPaidOrderConfirmedCustomerEmail(emailPayload);
            orderEmailService.sendOrderAdminNotification(emailPayload, "New prepaid order confirmed.");

            return new VerifyRazorpayPaymentResponse(
                    "Payment successful. Your order has been confirmed.",
                    order.getId(),
                    order.getOrderNumber(),
                    order.getPaymentStatus().name()
            );

        } catch (Exception e) {
            throw new RuntimeException("Payment verification failed: " + e.getMessage(), e);
        }
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateOrderNumber() {
        return "TF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}