package com.inventory.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProcurementClient {

    private final RestTemplate restTemplate;
    private final String procurementServiceUrl;

    public ProcurementClient(RestTemplate restTemplate,
                             @Value("${procurement-service.url}") String procurementServiceUrl) {
        this.restTemplate = restTemplate;
        this.procurementServiceUrl = procurementServiceUrl;
    }

    public Object createPurchaseRequest(ProcurementRequest request) {
        try {
            return restTemplate.postForObject(
                    procurementServiceUrl + "/procurement/request",
                    request,
                    Object.class
            );
        } catch (RestClientException ex) {
            throw new IllegalStateException("Procurement service is unavailable", ex);
        }
    }

    public static class ProcurementRequest {
        private Long productId;
        private Long warehouseId;
        private String productName;
        private Integer currentStock;
        private Integer requestedQuantity;
        private Long supplierId;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Long getWarehouseId() { return warehouseId; }
        public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getRequestedQuantity() { return requestedQuantity; }
        public void setRequestedQuantity(Integer requestedQuantity) { this.requestedQuantity = requestedQuantity; }
        public Long getSupplierId() { return supplierId; }
        public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    }
}
