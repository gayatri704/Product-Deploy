package com.example.productjpa.controller;

import com.example.productjpa.entity.User;
import com.example.productjpa.service.CartService;
import com.example.productjpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String viewCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("cartTotal", cartService.getCartTotal(user));
        return "cart/view";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        try {
            cartService.addToCart(user, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Added to cart!");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/shop/" + productId;
    }

    @PostMapping("/cart/{id}/update")
    public String updateQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam int quantity,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        try {
            cartService.updateQuantity(user, id, quantity);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/{id}/remove")
    public String removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findByUsername(userDetails.getUsername());
        cartService.removeItem(user, id);
        return "redirect:/cart";
    }
}
