package com.example.productjpa.controller;

import com.example.productjpa.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartService cartService;

    @Autowired
    private com.example.productjpa.service.UserService userService;

    @ModelAttribute("cartCount")
    public int cartCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return 0;
        }
        try {
            return cartService.getCartCount(userService.findByUsername(userDetails.getUsername()));
        } catch (Exception e) {
            return 0;
        }
    }
}
