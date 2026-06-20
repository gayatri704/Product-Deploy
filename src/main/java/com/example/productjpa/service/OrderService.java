package com.example.productjpa.service;

import com.example.productjpa.entity.CartItem;
import com.example.productjpa.entity.Order;
import com.example.productjpa.entity.OrderItem;
import com.example.productjpa.entity.User;
import com.example.productjpa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Order getOrderForUser(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized order access");
        }
        return order;
    }

    @Transactional
    public Order placeOrder(User user, String shippingAddress) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }
        if (shippingAddress == null || shippingAddress.isBlank()) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        Order order = Order.builder()
                .user(user)
                .shippingAddress(shippingAddress.trim())
                .status("CONFIRMED")
                .createdAt(LocalDateTime.now())
                .build();

        int total = 0;
        for (CartItem cartItem : cartItems) {
            productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(cartItem.getProduct().getPrice())
                    .build();
            order.getItems().add(orderItem);
            total += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        cartService.clearCart(user);
        return saved;
    }
}
