package com.example.productjpa.controller;

import com.example.productjpa.config.PasswordConfig;
import com.example.productjpa.config.SecurityConfig;
import com.example.productjpa.entity.Product;
import com.example.productjpa.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@Import({SecurityConfig.class, PasswordConfig.class})
class ProductControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private com.example.productjpa.service.UserService userService;

    @MockitoBean
    private com.example.productjpa.service.CartService cartService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductPassesNonEmptyMultipartFile() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "imageFile", "photo.png", MediaType.IMAGE_PNG_VALUE, new byte[]{1, 2, 3});

        mockMvc.perform(multipart("/admin/products")
                        .file(image)
                        .param("name", "Widget")
                        .param("price", "9")
                        .param("stock", "2")
                        .param("description", "Test")
                        .param("category", "Electronics")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        ArgumentCaptor<MultipartFile> fileCaptor = ArgumentCaptor.forClass(MultipartFile.class);
        verify(productService).addProduct(any(Product.class), fileCaptor.capture());
        assertThat(fileCaptor.getValue().isEmpty()).isFalse();
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo("photo.png");
    }
}
