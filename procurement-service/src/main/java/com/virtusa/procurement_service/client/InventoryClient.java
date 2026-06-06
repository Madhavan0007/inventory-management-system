package com.virtusa.procurement_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryClient {

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public InventoryClient(RestTemplate restTemplate,
                           @Value("${services.inventory.url}") String inventoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    public void inbound(Long productId, Long warehouseId, Integer quantity) {
        StockRequest request = new StockRequest();
        request.setProductId(productId);
        request.setWarehouseId(warehouseId);
        request.setQuantity(quantity);

        try {
            restTemplate.postForEntity(inventoryServiceUrl + "/inventory/inbound", request, Object.class);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Inventory service is unavailable", ex);
        }
    }

    public static class StockRequest {
        private Long productId;
        private Long warehouseId;
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getWarehouseId() { return warehouseId; }
        public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
