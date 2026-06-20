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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    public static final List<String> CATEGORIES = List.of(
            "Electronics", "Fashion", "Home", "Sports", "Books", "Beauty"
    );

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads")
            .toAbsolutePath()
            .normalize();

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> search(String keyword, String category) {
        List<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            String trimmed = keyword.trim();
            products = productRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    trimmed, trimmed);
        } else {
            products = findAll();
        }

        if (category != null && !category.isBlank() && !"All".equalsIgnoreCase(category)) {
            String cat = category.trim();
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().equalsIgnoreCase(cat))
                    .collect(Collectors.toList());
        }
        return products;
    }

    public void addProduct(Product product, MultipartFile imageFile) {
        if (product.getCategory() == null || product.getCategory().isBlank()) {
            product.setCategory("General");
        }
        storeImageIfPresent(product, imageFile);
        productRepository.save(product);
    }

    public void updateProduct(Long id, Product updated, MultipartFile imageFile) {
        Product existing = findById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        storeImageIfPresent(existing, imageFile);
        productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product product = findById(id);
        deleteImageIfPresent(product.getImageName());
        productRepository.delete(product);
    }

    public void reduceStock(Long productId, int quantity) {
        Product product = findById(productId);
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock for " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    private void storeImageIfPresent(Product product, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }

        String original = imageFile.getOriginalFilename();
        if (original == null || original.isBlank()) {
            original = "image";
        }
        String safeLeaf = Paths.get(original).getFileName().toString();
        String fileName = UUID.randomUUID() + "_" + safeLeaf;
        Path target = uploadDir.resolve(fileName);

        try (InputStream in = imageFile.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }

        deleteImageIfPresent(product.getImageName());
        product.setImageName(fileName);
    }

    private void deleteImageIfPresent(String imageName) {
        if (imageName == null || imageName.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(uploadDir.resolve(imageName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image: " + imageName, e);
        }
    }
}
