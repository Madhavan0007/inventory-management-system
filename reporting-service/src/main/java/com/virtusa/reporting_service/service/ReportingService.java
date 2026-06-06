package com.virtusa.reporting_service.service;

import com.virtusa.reporting_service.dto.*;

import java.util.List;

public interface ReportingService {

    List<InventoryValuationResponse>
    getInventoryValuation();

    StockMovementResponse
    getStockMovementReport();

    List<WarehousePerformanceResponse>
    getWarehousePerformance();

    List<ForecastResponse>
    getForecast();
}