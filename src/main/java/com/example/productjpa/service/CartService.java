package com.example.productjpa.service;

import com.example.productjpa.entity.CartItem;
import com.example.productjpa.entity.Product;
import com.example.productjpa.entity.User;
import com.example.productjpa.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUserOrderByIdAsc(user);
    }

    public int getCartCount(User user) {
        return cartItemRepository.findByUserOrderByIdAsc(user).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public int getCartTotal(User user) {
        return getCartItems(user).stream()
                .mapToInt(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    @Transactional
    public void addToCart(User user, Long productId, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        Product product = productService.findById(productId);
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElse(CartItem.builder().user(user).product(product).quantity(0).build());

        int newQty = item.getQuantity() + quantity;
        if (newQty > product.getStock()) {
            throw new IllegalArgumentException("Cannot add more than available stock");
        }
        item.setQuantity(newQty);
        cartItemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(User user, Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized cart access");
        }
        if (quantity < 1) {
            cartItemRepository.delete(item);
            return;
        }
        if (quantity > item.getProduct().getStock()) {
            throw new IllegalArgumentException("Not enough stock available");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(User user, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized cart access");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
}
