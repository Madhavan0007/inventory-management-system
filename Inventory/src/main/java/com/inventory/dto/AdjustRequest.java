package com.inventory.dto;

public class AdjustRequest {
    private Long productId;
    private Long warehouseId;
    private Integer newQuantity;
    private String reason;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Integer getNewQuantity() { return newQuantity; }
    public void setNewQuantity(Integer newQuantity) { this.newQuantity = newQuantity; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
