package com.inventory.service;

import com.inventory.client.ProductClient;
import com.inventory.client.ProductDetails;
import com.inventory.client.ProcurementClient;
import com.inventory.client.WarehouseClient;
import com.inventory.dto.AdjustRequest;
import com.inventory.dto.InventoryDto;
import com.inventory.dto.LowStockDto;
import com.inventory.dto.MinimumStockRequest;
import com.inventory.dto.ReplenishmentRequest;
import com.inventory.dto.StockRequest;
import com.inventory.dto.TransferRequest;
import com.inventory.entity.Inventory;
import com.inventory.entity.InventoryTransaction;
import com.inventory.entity.InventoryTransaction.TransactionType;
import com.inventory.entity.StockTransfer;
import com.inventory.entity.StockTransfer.TransferStatus;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ProductNotFoundException;
import com.inventory.repository.InventoryRepository;
import com.inventory.repository.InventoryTransactionRepository;
import com.inventory.repository.StockTransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final InventoryTransactionRepository transactionRepo;
    private final StockTransferRepository transferRepo;
    private final ProductClient productClient;
    private final WarehouseClient warehouseClient;
    private final ProcurementClient procurementClient;

    public InventoryService(InventoryRepository inventoryRepo,
                            InventoryTransactionRepository transactionRepo,
                            StockTransferRepository transferRepo,
                            ProductClient productClient,
                            WarehouseClient warehouseClient,
                            ProcurementClient procurementClient) {
        this.inventoryRepo = inventoryRepo;
        this.transactionRepo = transactionRepo;
        this.transferRepo = transferRepo;
        this.productClient = productClient;
        this.warehouseClient = warehouseClient;
        this.procurementClient = procurementClient;
    }

    public List<InventoryDto> getAllInventory() {
        return inventoryRepo.findAll().stream()
                .map(inv -> new InventoryDto(
                        inv.getProductId(),
                        inv.getWarehouseId(),
                        inv.getQuantity(),
                        inv.getMinimumStock()))
                .toList();
    }

    public Inventory getInventory(Long productId, Long warehouseId) {
        return inventoryRepo.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "No inventory for product=" + productId + " warehouse=" + warehouseId));
    }

    @Transactional
    public Inventory inbound(StockRequest req) {
        validateStockRequest(req);
        productClient.ensureProductExists(req.getProductId());
        warehouseClient.ensureWarehouseCanReceive(req.getWarehouseId(), req.getQuantity());

        Inventory inv = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setProductId(req.getProductId());
                    newInv.setWarehouseId(req.getWarehouseId());
                    return newInv;
                });
        inv.setQuantity(inv.getQuantity() + req.getQuantity());
        inventoryRepo.save(inv);
        warehouseClient.increaseUtilization(req.getWarehouseId(), req.getQuantity());
        recordTransaction(req.getProductId(), req.getWarehouseId(), TransactionType.INBOUND, req.getQuantity());
        return inv;
    }

    @Transactional
    public Inventory outbound(StockRequest req) {
        validateStockRequest(req);
        productClient.ensureProductExists(req.getProductId());
        warehouseClient.ensureWarehouseIsActive(req.getWarehouseId());
        Inventory inv = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
                .orElseThrow(() -> new ProductNotFoundException("Inventory not found"));
        if (inv.getQuantity() < req.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + inv.getQuantity() + ", Requested: " + req.getQuantity());
        }
        inv.setQuantity(inv.getQuantity() - req.getQuantity());
        inventoryRepo.save(inv);
        warehouseClient.decreaseUtilization(req.getWarehouseId(), req.getQuantity());
        recordTransaction(req.getProductId(), req.getWarehouseId(), TransactionType.OUTBOUND, req.getQuantity());
        return inv;
    }

    @Transactional
    public Inventory adjust(AdjustRequest req) {
        validateAdjustRequest(req);
        productClient.ensureProductExists(req.getProductId());
        warehouseClient.ensureWarehouseIsActive(req.getWarehouseId());
        Inventory inv = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
                .orElseThrow(() -> new ProductNotFoundException("Inventory not found"));
        int diff = req.getNewQuantity() - inv.getQuantity();
        if (diff > 0) {
            warehouseClient.ensureWarehouseCanReceive(req.getWarehouseId(), diff);
        }

        inv.setQuantity(req.getNewQuantity());
        inventoryRepo.save(inv);
        if (diff > 0) {
            warehouseClient.increaseUtilization(req.getWarehouseId(), diff);
        } else if (diff < 0) {
            warehouseClient.decreaseUtilization(req.getWarehouseId(), Math.abs(diff));
        }
        recordTransaction(req.getProductId(), req.getWarehouseId(), TransactionType.ADJUSTMENT, Math.abs(diff));
        return inv;
    }

    @Transactional
    public StockTransfer transfer(TransferRequest req) {
        validateTransferRequest(req);
        productClient.ensureProductExists(req.getProductId());
        warehouseClient.ensureWarehouseIsActive(req.getSourceWarehouseId());
        warehouseClient.ensureWarehouseCanReceive(req.getDestinationWarehouseId(), req.getQuantity());

        // Deduct from source
        Inventory source = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getSourceWarehouseId())
                .orElseThrow(() -> new ProductNotFoundException("Source inventory not found"));
        if (source.getQuantity() < req.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock in source warehouse. Available: " + source.getQuantity());
        }
        source.setQuantity(source.getQuantity() - req.getQuantity());
        inventoryRepo.save(source);
        warehouseClient.decreaseUtilization(req.getSourceWarehouseId(), req.getQuantity());

        // Add to destination
        Inventory dest = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getDestinationWarehouseId())
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setProductId(req.getProductId());
                    newInv.setWarehouseId(req.getDestinationWarehouseId());
                    return newInv;
                });
        dest.setQuantity(dest.getQuantity() + req.getQuantity());
        inventoryRepo.save(dest);
        warehouseClient.increaseUtilization(req.getDestinationWarehouseId(), req.getQuantity());

        // Record transfer
        StockTransfer transfer = new StockTransfer();
        transfer.setProductId(req.getProductId());
        transfer.setSourceWarehouseId(req.getSourceWarehouseId());
        transfer.setDestinationWarehouseId(req.getDestinationWarehouseId());
        transfer.setQuantity(req.getQuantity());
        transfer.setStatus(TransferStatus.COMPLETED);
        transferRepo.save(transfer);

        // Record transactions for both warehouses
        recordTransaction(req.getProductId(), req.getSourceWarehouseId(), TransactionType.OUTBOUND, req.getQuantity());
        recordTransaction(req.getProductId(), req.getDestinationWarehouseId(), TransactionType.INBOUND, req.getQuantity());

        return transfer;
    }

    public List<StockTransfer> getAllTransfers() {
        return transferRepo.findAll();
    }

    public List<InventoryTransaction> getTransactions(Long productId, Long warehouseId) {
        return transactionRepo.findByProductIdAndWarehouseId(productId, warehouseId);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    @Transactional
    public Inventory updateMinimumStock(MinimumStockRequest req) {
        validateMinimumStockRequest(req);
        productClient.ensureProductExists(req.getProductId());
        warehouseClient.ensureWarehouseIsActive(req.getWarehouseId());

        Inventory inv = inventoryRepo.findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setProductId(req.getProductId());
                    newInv.setWarehouseId(req.getWarehouseId());
                    return newInv;
                });

        inv.setMinimumStock(req.getMinimumStock());
        return inventoryRepo.save(inv);
    }

    public List<LowStockDto> getLowStockItems() {
        return inventoryRepo.findAll()
                .stream()
                .filter(inv -> inv.getQuantity() <= inv.getMinimumStock())
                .map(inv -> new LowStockDto(
                        inv.getProductId(),
                        inv.getWarehouseId(),
                        inv.getQuantity(),
                        inv.getMinimumStock()
                ))
                .toList();
    }

    public Object createReplenishmentRequest(ReplenishmentRequest req) {
        validateReplenishmentRequest(req);

        ProductDetails product = productClient.getProduct(req.getProductId());
        Inventory inv = getInventory(req.getProductId(), req.getWarehouseId());

        int requestedQuantity;
        if (req.getRequestedQuantity() == null) {
            if (inv.getQuantity() > inv.getMinimumStock()) {
                throw new IllegalArgumentException("Inventory is above the minimum stock threshold");
            }
            requestedQuantity = inv.getQuantity().equals(inv.getMinimumStock())
                    ? Math.max(inv.getMinimumStock(), 1)
                    : Math.max(inv.getMinimumStock() - inv.getQuantity(), 1);
        } else {
            requestedQuantity = req.getRequestedQuantity();
        }

        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be greater than zero");
        }

        ProcurementClient.ProcurementRequest procurementRequest =
                new ProcurementClient.ProcurementRequest();
        procurementRequest.setProductId(req.getProductId());
        procurementRequest.setWarehouseId(req.getWarehouseId());
        procurementRequest.setProductName(product.getName());
        procurementRequest.setCurrentStock(inv.getQuantity());
        procurementRequest.setRequestedQuantity(requestedQuantity);
        procurementRequest.setSupplierId(req.getSupplierId());

        return procurementClient.createPurchaseRequest(procurementRequest);
    }

    private void recordTransaction(Long productId, Long warehouseId, TransactionType type, int qty) {
        InventoryTransaction tx = new InventoryTransaction();
        tx.setProductId(productId);
        tx.setWarehouseId(warehouseId);
        tx.setType(type);
        tx.setQuantity(qty);
        transactionRepo.save(tx);
    }

    private void validateStockRequest(StockRequest req) {
        if (req == null || req.getProductId() == null || req.getWarehouseId() == null) {
            throw new IllegalArgumentException("Product id and warehouse id are required");
        }
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private void validateMinimumStockRequest(MinimumStockRequest req) {
        if (req == null || req.getProductId() == null || req.getWarehouseId() == null) {
            throw new IllegalArgumentException("Product id and warehouse id are required");
        }
        if (req.getMinimumStock() == null || req.getMinimumStock() < 0) {
            throw new IllegalArgumentException("Minimum stock cannot be negative");
        }
    }

    private void validateReplenishmentRequest(ReplenishmentRequest req) {
        if (req == null || req.getProductId() == null || req.getWarehouseId() == null || req.getSupplierId() == null) {
            throw new IllegalArgumentException("Product id, warehouse id, and supplier id are required");
        }
        if (req.getRequestedQuantity() != null && req.getRequestedQuantity() <= 0) {
            throw new IllegalArgumentException("Requested quantity must be greater than zero");
        }
    }

    private void validateAdjustRequest(AdjustRequest req) {
        if (req == null || req.getProductId() == null || req.getWarehouseId() == null) {
            throw new IllegalArgumentException("Product id and warehouse id are required");
        }
        if (req.getNewQuantity() == null || req.getNewQuantity() < 0) {
            throw new IllegalArgumentException("New quantity cannot be negative");
        }
    }

    private void validateTransferRequest(TransferRequest req) {
        if (req == null || req.getProductId() == null
                || req.getSourceWarehouseId() == null || req.getDestinationWarehouseId() == null) {
            throw new IllegalArgumentException("Product id, source warehouse id, and destination warehouse id are required");
        }
        if (req.getSourceWarehouseId().equals(req.getDestinationWarehouseId())) {
            throw new IllegalArgumentException("Source and destination warehouses must be different");
        }
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
