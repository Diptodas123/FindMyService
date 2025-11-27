package com.FindMyService.controller;

import com.FindMyService.model.Order;
import com.FindMyService.model.enums.OrderStatus;
import com.FindMyService.service.OrderService;
import com.FindMyService.utils.ErrorResponseBuilder;
import com.FindMyService.utils.OwnerCheck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/orders")
@RestController
public class OrderController {

    private final OrderService orderService;
    private final OwnerCheck ownerCheck;

    public OrderController(OrderService orderService, OwnerCheck ownerCheck) {
        this.orderService = orderService;
        this.ownerCheck = ownerCheck;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .map(order -> ResponseEntity.ok((Object) order))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            ownerCheck.verifyOwner(order.getUserId().getUserId());
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to create this order"));
        }
        return orderService.createOrder(order);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        boolean orderToDelete = orderService.deleteOrder(orderId);
        if (orderToDelete) {
            return ResponseEntity.ok(ErrorResponseBuilder.ok("Order deleted successfully"));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found with id: " + orderId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        try {
            ownerCheck.verifyOwner(userId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to access these orders"));
        }
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/provider/{providerId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('PROVIDER')")
    public ResponseEntity<?> getOrdersByProvider(@PathVariable Long providerId) {
        try {
            ownerCheck.verifyOwner(providerId);
        } catch (AccessDeniedException ex) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ErrorResponseBuilder.forbidden("You are not authorized to access these orders"));
        }
        return orderService.getOrdersByProvider(providerId);
    }

    @PostMapping("/{orderId}/payment/initiate")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> initiatePayment(@PathVariable Long orderId) {
        return orderService.initiatePayment(orderId);
    }

    @PostMapping("/{orderId}/payment/confirm")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> confirmPayment(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> payload) {
        return orderService.confirmPayment(orderId, payload.get("paymentIntentId"));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatus newStatus) {
        return orderService.getOrderById(orderId)
                .map(order -> {
                    try {
                        ownerCheck.verifyOwner(order.getProviderId().getProviderId());
                        return orderService.updateOrderStatus(orderId, newStatus);
                    } catch (AccessDeniedException ex) {
                        return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ErrorResponseBuilder.forbidden("You are not authorized to update this order"));
                    }
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ErrorResponseBuilder.build(HttpStatus.NOT_FOUND, "Order not found")));
    }
}
