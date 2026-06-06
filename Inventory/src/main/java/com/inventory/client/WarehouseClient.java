package com.inventory.client;

import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.WarehouseNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class WarehouseClient {

    private final RestTemplate restTemplate;
    private final String warehouseServiceUrl;

    public WarehouseClient(RestTemplate restTemplate,
                           @Value("${warehouse-service.url}") String warehouseServiceUrl) {
        this.restTemplate = restTemplate;
        this.warehouseServiceUrl = warehouseServiceUrl;
    }

    public void ensureWarehouseCanReceive(Long warehouseId, int quantity) {
        ensureWarehouseIsActive(warehouseId);

        Boolean hasCapacity = getForBoolean(
                warehouseServiceUrl + "/warehouses/{id}/capacity?qty={qty}",
                warehouseId,
                quantity
        );

        if (!Boolean.TRUE.equals(hasCapacity)) {
            throw new InsufficientStockException("Warehouse does not have enough available capacity");
        }
    }

    public void increaseUtilization(Long warehouseId, int quantity) {
        postForVoid(warehouseServiceUrl + "/warehouses/{id}/increase?qty={qty}", warehouseId, quantity);
    }

    public void decreaseUtilization(Long warehouseId, int quantity) {
        postForVoid(warehouseServiceUrl + "/warehouses/{id}/decrease?qty={qty}", warehouseId, quantity);
    }

    public void ensureWarehouseIsActive(Long warehouseId) {
        Boolean active = getForBoolean(warehouseServiceUrl + "/warehouses/{id}/active", warehouseId);
        if (!Boolean.TRUE.equals(active)) {
            throw new WarehouseNotFoundException("Warehouse is not active or does not exist: " + warehouseId);
        }
    }

    private Boolean getForBoolean(String url, Object... variables) {
        try {
            return restTemplate.getForObject(url, Boolean.class, variables);
        } catch (HttpStatusCodeException ex) {
            handleWarehouseHttpError(ex);
            return false;
        } catch (RestClientException ex) {
            throw new IllegalStateException("Warehouse service is unavailable", ex);
        }
    }

    private void postForVoid(String url, Object... variables) {
        try {
            restTemplate.postForEntity(url, null, Void.class, variables);
        } catch (HttpStatusCodeException ex) {
            handleWarehouseHttpError(ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Warehouse service is unavailable", ex);
        }
    }

    private void handleWarehouseHttpError(HttpStatusCodeException ex) {
        HttpStatusCode status = ex.getStatusCode();
        if (status.value() == 404) {
            throw new WarehouseNotFoundException("Warehouse not found");
        }
        if (status.is4xxClientError()) {
            throw new InsufficientStockException("Warehouse capacity update failed");
        }
        throw ex;
    }
}
