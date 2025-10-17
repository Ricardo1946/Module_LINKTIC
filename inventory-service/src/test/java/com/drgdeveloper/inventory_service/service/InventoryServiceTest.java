package com.drgdeveloper.inventory_service.service;

import com.drgdeveloper.inventory_service.client.ProductClient;
import com.drgdeveloper.inventory_service.dto.PurchaseRequest;
import com.drgdeveloper.inventory_service.model.Inventory;
import com.drgdeveloper.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductClient productClient;

    private UUID testProductId;
    private Inventory existingInventory;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        // Simulación de un objeto Inventory existente con stock 100
        existingInventory = Inventory.builder()
                .id(UUID.randomUUID())
                .productId(testProductId)
                .quantity(100)
                .build();
    }

    @Test
    void testGetQuantity_InventoryFound_ShouldReturnQuantity() {
        // ARRANGE: Simular que el repositorio encuentra el inventario
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(existingInventory));

        // ACT
        int quantity = inventoryService.getQuantity(testProductId);

        // ASSERT: Verificar que la cantidad devuelta es 100
        assertEquals(100, quantity);
    }
    @Test
    void testGetQuantity_InventoryNotFound_ShouldReturnZero() {
        // ARRANGE: Simular que el repositorio NO encuentra el inventario
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.empty());

        // ACT
        int quantity = inventoryService.getQuantity(testProductId);

        // ASSERT: Verificar que la cantidad devuelta es 0 (gracias al .orElse(0))
        assertEquals(0, quantity);
    }

    @Test
    void testUpdateStock_ExistingProduct_ShouldUpdateQuantity() {
        int newQuantity = 50;
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(existingInventory));
        // Simular que el método save funciona
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);
        Inventory updatedInv = inventoryService.updateStock(testProductId, newQuantity);

        // ASSERT
        assertEquals(newQuantity, updatedInv.getQuantity(), "La cantidad debe ser 50.");
        // Verificar que el método save fue llamado una vez
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testPurchase_Success_ShouldDecreaseQuantity() {
        int purchaseQuantity = 25;
        int expectedRemaining = 75; // 100 - 25
        PurchaseRequest request = new PurchaseRequest(testProductId, purchaseQuantity);
        when(productClient.getProductById(testProductId)).thenReturn(Map.of("id", testProductId));
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(existingInventory);
        Map<String, Object> result = inventoryService.purchase(request);

        assertEquals(expectedRemaining, existingInventory.getQuantity(), "El stock restante debe ser 75.");
        assertEquals(testProductId, result.get("productId"));
        assertEquals(purchaseQuantity, result.get("purchasedQuantity"));

        verify(inventoryRepository, times(1)).save(existingInventory);
    }
    @Test
    void testPurchase_InsufficientStock_ShouldThrowException() {
        int purchaseQuantity = 101; // Más que el stock (100)
        PurchaseRequest request = new PurchaseRequest(testProductId, purchaseQuantity);
        when(productClient.getProductById(testProductId)).thenReturn(Map.of("id", testProductId));
        // 2. Simular que el repositorio encuentra el inventario (100)
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(existingInventory));

        // ACT & ASSERT: Esperar que se lance la RuntimeException con el mensaje específico
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.purchase(request);
        });
        assertEquals("Insufficient inventory", thrown.getMessage());

        // Verificar que NO se llamó al método save, ya que la transacción falló
        verify(inventoryRepository, never()).save(any());
    }
    @Test
    void testPurchase_InventoryNotFound_ShouldThrowException() {
        PurchaseRequest request = new PurchaseRequest(testProductId, 1);
        when(productClient.getProductById(testProductId)).thenReturn(Map.of("id", testProductId));
        // 2. Simular que el inventario NO existe en el repositorio
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.empty());

        // ACT & ASSERT: Esperar que se lance la RuntimeException
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            inventoryService.purchase(request);
        });
        assertEquals("Inventory not found", thrown.getMessage());

        // Verificar que NO se llamó al método save
        verify(inventoryRepository, never()).save(any());
    }
    }