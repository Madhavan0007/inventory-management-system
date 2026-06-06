package com.virtusa.warehouse_service.controller;

import com.virtusa.warehouse_service.dto.WarehouseRequest;
import com.virtusa.warehouse_service.dto.WarehouseResponse;
import com.virtusa.warehouse_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public WarehouseResponse create(@RequestBody WarehouseRequest request) {
        return warehouseService.createWarehouse(request);
    }

    @GetMapping
    public List<WarehouseResponse> getAll() {
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    public WarehouseResponse getById(@PathVariable Long id) {
        return warehouseService.getWarehouseById(id);
    }

    @PutMapping("/{id}")
    public WarehouseResponse update(@PathVariable Long id,
                                    @RequestBody WarehouseRequest request) {
        return warehouseService.updateWarehouse(id, request);
    }

    @GetMapping("/{id}/active")
    public boolean isActive(@PathVariable Long id) {
        return warehouseService.isWarehouseActive(id);
    }

    @GetMapping("/{id}/capacity")
    public boolean hasCapacity(@PathVariable Long id,
                               @RequestParam int qty) {
        return warehouseService.hasCapacity(id, qty);
    }

    @PostMapping("/{id}/increase")
    public void increase(@PathVariable Long id,
                         @RequestParam int qty) {
        warehouseService.increaseUtilization(id, qty);
    }

    @PostMapping("/{id}/decrease")
    public void decrease(@PathVariable Long id,
                         @RequestParam int qty) {
        warehouseService.decreaseUtilization(id, qty);
    }
}