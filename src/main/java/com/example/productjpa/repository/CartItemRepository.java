package com.example.productjpa.repository;

import com.example.productjpa.entity.CartItem;
import com.example.productjpa.entity.Product;
import com.example.productjpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserOrderByIdAsc(User user);

    Optional<CartItem> findByUserAndProduct(User user, Product product);

    int countByUser(User user);

    void deleteByUser(User user);
}
