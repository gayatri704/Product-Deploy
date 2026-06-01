package com.example.productjpa.service;

import com.example.productjpa.entity.Product;
import com.example.productjpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public void addProduct(Product product, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            Path dir = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory: " + dir, e);
            }
            String original = imageFile.getOriginalFilename();
            if (original == null || original.isBlank()) {
                original = "image";
            }
            String safeLeaf = Paths.get(original).getFileName().toString();
            String fileName = UUID.randomUUID() + "_" + safeLeaf;
            Path target = dir.resolve(fileName);
            try (InputStream in = imageFile.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Image upload failed", e);
            }
            product.setImageName(fileName);
        }
        productRepository.save(product);
    }

    public List<Product> findProduct(String name) {
        return productRepository.findAllByNameContaining(name);
    }

    public Product findById(Long id) {
       Product product = productRepository.findById(id).orElseThrow();
        return product;
    }
}
