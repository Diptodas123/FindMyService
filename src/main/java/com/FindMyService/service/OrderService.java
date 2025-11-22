package com.FindMyService.service;

import com.FindMyService.model.Order;
import com.FindMyService.repository.OrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(Long orderId, Order order) {
        Order existingOrder = orderRepository.findById(orderId).orElse(null);
        if (existingOrder == null) {
            return Optional.empty();
        }
        order.setOrderId(orderId);
        Order updatedOrder = orderRepository.save(order);
        return Optional.of(updatedOrder);
    }

    public boolean deleteOrder(Long orderId) {
        return orderRepository.findById(orderId).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }

    public boolean payOrder(Long orderId) {       // todo
        // placeholder for initiating/recording payment
        return false;
    }
}
