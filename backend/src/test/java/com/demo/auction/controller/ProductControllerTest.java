package com.demo.auction.controller;

import com.demo.auction.model.Product;
import com.demo.auction.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = Product.builder()
                .id(1L)
                .name("Vintage Guitar")
                .description("1967 Fender Stratocaster")
                .startingPrice(new BigDecimal("2500.00"))
                .category("Music")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("GET /v1/api/products - should return all products")
    void findAll_shouldReturnAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/v1/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Vintage Guitar")))
                .andExpect(jsonPath("$[0].category", is("Music")));
    }

    @Test
    @DisplayName("GET /v1/api/products?activeOnly=true - should return only active")
    void findAll_withActiveOnly_shouldReturnActiveProducts() throws Exception {
        when(productService.findActive()).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/v1/api/products").param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active", is(true)));
    }

    @Test
    @DisplayName("GET /v1/api/products/{id} - found - should return 200")
    void findById_found_shouldReturn200() throws Exception {
        when(productService.findById(1L)).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(get("/v1/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Vintage Guitar")));
    }

    @Test
    @DisplayName("GET /v1/api/products/{id} - not found - should return 404")
    void findById_notFound_shouldReturn404() throws Exception {
        when(productService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /v1/api/products - should create product and return 201")
    void create_shouldReturnCreated() throws Exception {
        Product newProduct = Product.builder()
                .name("Classic Watch")
                .description("Swiss movement")
                .startingPrice(new BigDecimal("1200.00"))
                .category("Jewelry")
                .active(true)
                .build();

        when(productService.save(any(Product.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/v1/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @DisplayName("DELETE /v1/api/products/{id} - found - should return 204")
    void delete_found_shouldReturn204() throws Exception {
        when(productService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/v1/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /v1/api/products/{id} - not found - should return 404")
    void delete_notFound_shouldReturn404() throws Exception {
        when(productService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/v1/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /v1/api/products/{id} - found - should update and return 200")
    void update_found_shouldReturn200() throws Exception {
        when(productService.update(eq(1L), any(Product.class)))
                .thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(put("/v1/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isOk());
    }
}
