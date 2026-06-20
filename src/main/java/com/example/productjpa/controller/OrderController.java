package com.example.productjpa.controller;

import com.example.productjpa.entity.User;
import com.example.productjpa.service.CartService;
import com.example.productjpa.service.OrderService;
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
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (cartService.getCartItems(user).isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cartItems", cartService.getCartItems(user));
        model.addAttribute("cartTotal", cartService.getCartTotal(user));
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String shippingAddress,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByUsername(userDetails.getUsername());
        try {
            var order = orderService.placeOrder(user, shippingAddress);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/orders/" + order.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/orders")
    public String orderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("orders", orderService.getOrdersForUser(user));
        return "order/history";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("order", orderService.getOrderForUser(user, id));
        return "order/detail";
    }
}
