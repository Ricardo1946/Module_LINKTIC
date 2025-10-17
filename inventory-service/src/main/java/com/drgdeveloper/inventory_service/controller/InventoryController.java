package com.drgdeveloper.inventory_service.controller;


import com.drgdeveloper.inventory_service.dto.PurchaseRequest;
import com.drgdeveloper.inventory_service.service.InventoryService;
import com.drgdeveloper.inventory_service.util.JsonApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("app/v1")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService service) {
        this.inventoryService = service;
    }

    @GetMapping("/inventory/{productoId}")
    public ResponseEntity<JsonApiResponse> getStock(@PathVariable UUID productoId) {
        int cantidad = inventoryService.getQuantity(productoId);
        return ResponseEntity.ok(new JsonApiResponse(Map.of("stock", cantidad)));
    }

    @PatchMapping("/inventory/{productoId}")
    public ResponseEntity<JsonApiResponse> updateStock(@PathVariable UUID productoId, @RequestBody Map<String, Integer> body) {
        int newStock = body.getOrDefault("quantity", 0);
        return ResponseEntity.ok(new JsonApiResponse(inventoryService.updateStock(productoId, newStock)));
    }

    @PostMapping("/inventory/purchase")
    public ResponseEntity<JsonApiResponse> purchase(@RequestBody @Valid PurchaseRequest request) {
        Map<String, Object> result = inventoryService.purchase(request);
        return ResponseEntity.ok(new JsonApiResponse(result));
    }
}
