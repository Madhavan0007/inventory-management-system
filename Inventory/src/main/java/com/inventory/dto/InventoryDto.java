package com.inventory.dto;

public class InventoryDto {

    private Long productId;
    private Long warehouseId;
    private Integer quantity;
    private Integer minimumStock;

    public InventoryDto() {}

    public InventoryDto(Long productId, Long warehouseId, Integer quantity, Integer minimumStock) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.minimumStock = minimumStock;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }
}
