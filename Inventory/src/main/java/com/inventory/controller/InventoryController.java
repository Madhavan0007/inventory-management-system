package com.inventory.controller;

import com.inventory.dto.AdjustRequest;
import com.inventory.dto.InventoryDto;
import com.inventory.dto.LowStockDto;
import com.inventory.dto.MinimumStockRequest;
import com.inventory.dto.ReplenishmentRequest;
import com.inventory.dto.StockRequest;
import com.inventory.dto.TransferRequest;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryTransaction;
import com.inventory.entity.StockTransfer;
import com.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory API", description = "Inventory operations: inbound, outbound, adjust, transfer")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    @Operation(summary = "Get all inventory")
    public ResponseEntity<List<InventoryDto>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    @Operation(summary = "Get inventory by product and warehouse")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long productId, @PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventory(productId, warehouseId));
    }

    @PostMapping("/inbound")
    @Operation(summary = "Add inbound stock")
    public ResponseEntity<Inventory> inbound(@RequestBody StockRequest request) {
        return ResponseEntity.ok(inventoryService.inbound(request));
    }

    @PostMapping("/outbound")
    @Operation(summary = "Remove outbound stock")
    public ResponseEntity<Inventory> outbound(@RequestBody StockRequest request) {
        return ResponseEntity.ok(inventoryService.outbound(request));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust stock to a specific quantity")
    public ResponseEntity<Inventory> adjust(@RequestBody AdjustRequest request) {
        return ResponseEntity.ok(inventoryService.adjust(request));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer stock between warehouses")
    public ResponseEntity<StockTransfer> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(inventoryService.transfer(request));
    }

    @GetMapping("/transfers")
    @Operation(summary = "Get all stock transfers")
    public ResponseEntity<List<StockTransfer>> getTransfers() {
        return ResponseEntity.ok(inventoryService.getAllTransfers());
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transactions by product and warehouse")
    public ResponseEntity<List<InventoryTransaction>> getTransactions(
            @RequestParam Long productId,
            @RequestParam Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getTransactions(productId, warehouseId));
    }

    @GetMapping("/transactions/all")
    @Operation(summary = "Get all inventory transactions")
    public ResponseEntity<List<InventoryTransaction>> getAllTransactions() {
        return ResponseEntity.ok(inventoryService.getAllTransactions());
    }

    @PutMapping("/minimum-stock")
    @Operation(summary = "Set minimum stock threshold for a product in a warehouse")
    public ResponseEntity<Inventory> updateMinimumStock(@RequestBody MinimumStockRequest request) {
        return ResponseEntity.ok(inventoryService.updateMinimumStock(request));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get inventory records at or below minimum stock")
    public ResponseEntity<List<LowStockDto>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PostMapping("/replenishment/request")
    @Operation(summary = "Create a procurement request for low stock")
    public ResponseEntity<Object> createReplenishmentRequest(@RequestBody ReplenishmentRequest request) {
        return ResponseEntity.ok(inventoryService.createReplenishmentRequest(request));
    }
}
