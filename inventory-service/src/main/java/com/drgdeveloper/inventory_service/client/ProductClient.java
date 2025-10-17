package com.drgdeveloper.inventory_service.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Component
public class ProductClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getProductById(UUID productId) {
        String url = "http://products-service:8081/app/v1/products/" + productId;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Product not found: " + productId);
        }
    }

}
