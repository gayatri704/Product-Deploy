package com.example.productjpa.controller;

import com.example.productjpa.entity.Product;
import com.example.productjpa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/shop")
    public String shop(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            Model model) {
        model.addAttribute("products", productService.search(keyword, category));
        model.addAttribute("categories", ProductService.CATEGORIES);
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("keyword", keyword.trim());
        }
        if (category != null && !category.isBlank()) {
            model.addAttribute("category", category.trim());
        }
        return "shop/list";
    }

    @GetMapping("/shop/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "shop/detail";
    }

    @GetMapping("/admin/products")
    public String adminList(Model model) {
        model.addAttribute("products", productService.findAll());
        return "admin/list";
    }

    @GetMapping("/admin/products/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", ProductService.CATEGORIES);
        model.addAttribute("isEdit", false);
        return "admin/form";
    }

    @PostMapping("/admin/products")
    public String create(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int price,
            @RequestParam(defaultValue = "0") int stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .description(description != null ? description : "")
                .category(category)
                .build();
        productService.addProduct(product, imageFile);
        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/{id}/edit")
    public String editForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", ProductService.CATEGORIES);
        model.addAttribute("isEdit", true);
        return "admin/form";
    }

    @PostMapping("/admin/products/{id}")
    public String update(
            @PathVariable("id") long id,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int price,
            @RequestParam(defaultValue = "0") int stock,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Product product = Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .description(description != null ? description : "")
                .category(category)
                .build();
        productService.updateProduct(id, product, imageFile);
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }
}
