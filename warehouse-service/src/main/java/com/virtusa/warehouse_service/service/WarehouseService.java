package com.virtusa.warehouse_service.service;

import com.virtusa.warehouse_service.dto.WarehouseRequest;
import com.virtusa.warehouse_service.dto.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);

    List<WarehouseResponse> getAllWarehouses();

    WarehouseResponse getWarehouseById(Long id);

    WarehouseResponse updateWarehouse(Long id, WarehouseRequest request);

    boolean isWarehouseActive(Long warehouseId);

    boolean hasCapacity(Long warehouseId, int incomingQuantity);

    void increaseUtilization(Long warehouseId, int quantity);

    void decreaseUtilization(Long warehouseId, int quantity);
}