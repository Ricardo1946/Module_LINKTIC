package com.drgdeveloper.inventory_service.service;

import com.drgdeveloper.inventory_service.client.ProductClient;
import com.drgdeveloper.inventory_service.dto.PurchaseRequest;
import com.drgdeveloper.inventory_service.model.Inventory;
import com.drgdeveloper.inventory_service.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductClient productClient;

    public InventoryService(InventoryRepository inventoryRepository, ProductClient productClient) {
        this.inventoryRepository = inventoryRepository;
        this.productClient = productClient;
    }

    public Inventory updateStock(UUID productId, int newQuantity) {
        Inventory inv = inventoryRepository.findByProductoId(productId)
                .orElseGet(() -> Inventory.builder()
                        .productId(productId)
                        .quantity(0)
                        .build());
        inv.setQuantity(newQuantity);
        return inventoryRepository.save(inv);
    }

    public int getQuantity(UUID productId) {
        return inventoryRepository.findByProductoId(productId)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

    public Map<String, Object> purchase(PurchaseRequest request) {
        productClient.getProductById(request.productId());
        Inventory inv = inventoryRepository.findByProductoId(request.productId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));
        if (inv.getQuantity() < request.quantity()) {
            throw new RuntimeException("Insufficient inventory");
        }

        inv.setQuantity(inv.getQuantity() - request.quantity());
        inventoryRepository.save(inv);

        Map<String, Object> result = new HashMap<>();
        result.put("productId", request.productId());
        result.put("purchasedQuantity", request.quantity());
        return result;

    }

}
