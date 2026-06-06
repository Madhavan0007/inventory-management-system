package com.virtusa.reporting_service.controller;


import com.virtusa.reporting_service.dto.*;
import com.virtusa.reporting_service.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    /*
     * Inventory Value Report
     *
     * quantity × unit price
     */
    @GetMapping("/inventory-valuation")
    public List<InventoryValuationResponse>
    inventoryValuation() {

        return reportingService
                .getInventoryValuation();
    }

    /*
     * Stock Movement Report
     */
    @GetMapping("/stock-movement")
    public StockMovementResponse
    stockMovement() {

        return reportingService
                .getStockMovementReport();
    }

    /*
     * Warehouse Performance Report
     */
    @GetMapping("/warehouse-performance")
    public List<WarehousePerformanceResponse>
    warehousePerformance() {

        return reportingService
                .getWarehousePerformance();
    }

    /*
     * Inventory Forecast Report
     */
    @GetMapping("/forecast")
    public List<ForecastResponse>
    forecast() {

        return reportingService
                .getForecast();
    }
}
