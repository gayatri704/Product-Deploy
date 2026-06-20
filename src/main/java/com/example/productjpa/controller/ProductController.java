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

    @GetMapping("/products")
    public String list(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("products", productService.searchByKeyword(keyword));
            model.addAttribute("keyword", keyword.trim());
        } else {
            model.addAttribute("products", productService.findAll());
        }
        return "product/list";
    }

    @GetMapping("/products/new")
    public String newForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("isEdit", false);
        return "product/form";
    }

    @PostMapping("/products")
    public String create(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int price,
            @RequestParam(defaultValue = "0") int stock,
            @RequestParam(required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description != null ? description : "");
        productService.addProduct(product, imageFile);
        return "redirect:/products";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable("id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "product/detail";
    }

    @GetMapping("/products/{id}/edit")
    public String editForm(@PathVariable("id") long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("isEdit", true);
        return "product/form";
    }

    @PostMapping("/products/{id}")
    public String update(
            @PathVariable("id") long id,
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int price,
            @RequestParam(defaultValue = "0") int stock,
            @RequestParam(required = false) String description,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description != null ? description : "");
        productService.updateProduct(id, product, imageFile);
        return "redirect:/products/" + id;
    }

    @PostMapping("/products/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
