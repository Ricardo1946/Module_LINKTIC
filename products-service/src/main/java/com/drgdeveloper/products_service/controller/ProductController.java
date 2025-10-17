package com.drgdeveloper.products_service.controller;


import com.drgdeveloper.products_service.client.JsonApiResponse;
import com.drgdeveloper.products_service.dto.ProductRequest;
import com.drgdeveloper.products_service.model.Product;
import com.drgdeveloper.products_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("app/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<JsonApiResponse> create(@RequestBody @Valid ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .description(request.description())
                .build();

        return ResponseEntity.ok(new JsonApiResponse(productService.create(product)));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<JsonApiResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(new JsonApiResponse(productService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<JsonApiResponse> getAll() {
        return ResponseEntity.ok(new JsonApiResponse(productService.findAll()));
    }

}
