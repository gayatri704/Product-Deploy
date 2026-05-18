package com.example.productjpa.controller;

import com.example.productjpa.entity.Product;
import com.example.productjpa.service.ProductService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/products")
        public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
        }

    @GetMapping("/products/new")
    public String newForm(){
        return "product/form";
    }

    @PostMapping("/products")
    public String createForm(
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
    @PostMapping("/findProduct")
    public String findProduct(@RequestParam("name") String name, Model model){
        List<Product> list = productService.findProduct(name);
        model.addAttribute("products", list);
        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Long id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/detail";
                })
                .orElse("redirect:/products");
    }
}
