package com.virtusa.warehouse_service.service.impl;

import com.virtusa.warehouse_service.dto.WarehouseRequest;
import com.virtusa.warehouse_service.dto.WarehouseResponse;
import com.virtusa.warehouse_service.entity.Warehouse;
import com.virtusa.warehouse_service.enums.WarehouseStatus;
import com.virtusa.warehouse_service.exception.WarehouseNotFoundException;
import com.virtusa.warehouse_service.repository.WarehouseRepository;
import com.virtusa.warehouse_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .capacity(request.getCapacity())
                .build();

        return mapToResponse(warehouseRepository.save(warehouse));
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = getWarehouseOrThrow(id);
        return mapToResponse(warehouse);
    }

    @Override
    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {
        Warehouse warehouse = getWarehouseOrThrow(id);

        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setCapacity(request.getCapacity());

        return mapToResponse(warehouseRepository.save(warehouse));
    }

    @Override
    public boolean isWarehouseActive(Long warehouseId) {
        return warehouseRepository.existsByIdAndStatus(warehouseId, WarehouseStatus.ACTIVE);
    }

    @Override
    public boolean hasCapacity(Long warehouseId, int incomingQuantity) {
        Warehouse warehouse = getWarehouseOrThrow(warehouseId);

        return warehouse.getCurrentUtilization() + incomingQuantity
                <= warehouse.getCapacity();
    }

    @Override
    public void increaseUtilization(Long warehouseId, int quantity) {
        Warehouse warehouse = getWarehouseOrThrow(warehouseId);

        if (warehouse.getCurrentUtilization() + quantity > warehouse.getCapacity()) {
            throw new IllegalStateException("Exceeds warehouse capacity");
        }

        warehouse.setCurrentUtilization(
                warehouse.getCurrentUtilization() + quantity
        );

        warehouseRepository.save(warehouse);
    }

    @Override
    public void decreaseUtilization(Long warehouseId, int quantity) {
        Warehouse warehouse = getWarehouseOrThrow(warehouseId);

        if (warehouse.getCurrentUtilization() - quantity < 0) {
            throw new IllegalStateException("Utilization cannot be negative");
        }

        warehouse.setCurrentUtilization(
                warehouse.getCurrentUtilization() - quantity
        );

        warehouseRepository.save(warehouse);
    }


    private Warehouse getWarehouseOrThrow(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found"));
    }

    private WarehouseResponse mapToResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .currentUtilization(warehouse.getCurrentUtilization())
                .status(warehouse.getStatus())
                .build();
    }
}