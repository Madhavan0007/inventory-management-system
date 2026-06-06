package com.virtusa.reporting_service.service;

import com.virtusa.reporting_service.client.InventoryClient;
import com.virtusa.reporting_service.client.ProductClient;
import com.virtusa.reporting_service.client.WarehouseClient;
import com.virtusa.reporting_service.dto.ForecastResponse;
import com.virtusa.reporting_service.dto.InventoryDto;
import com.virtusa.reporting_service.dto.InventoryTransactionDto;
import com.virtusa.reporting_service.dto.InventoryValuationResponse;
import com.virtusa.reporting_service.dto.ProductDto;
import com.virtusa.reporting_service.dto.StockMovementResponse;
import com.virtusa.reporting_service.dto.StockTransferDto;
import com.virtusa.reporting_service.dto.WarehousePerformanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final WarehouseClient warehouseClient;

    @Override
    public List<InventoryValuationResponse> getInventoryValuation() {
        Map<Long, ProductDto> productsById = productClient
                .getProducts()
                .stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        List<InventoryDto> inventories = inventoryClient.getInventory();

        return inventories.stream()
                .map(inventory -> {
                    ProductDto product = productsById.get(inventory.getProductId());
                    if (product == null) {
                        return null;
                    }

                    int quantity = inventory.getQuantity() == null ? 0 : inventory.getQuantity();
                    double unitPrice = product.getPrice() == null ? 0.0 : product.getPrice();

                    return InventoryValuationResponse
                            .builder()
                            .productName(product.getName())
                            .quantity(quantity)
                            .unitPrice(unitPrice)
                            .totalValue(quantity * unitPrice)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public StockMovementResponse getStockMovementReport() {
        List<InventoryTransactionDto> transactions =
                inventoryClient.getTransactions();
        List<StockTransferDto> transfers =
                inventoryClient.getTransfers();

        return StockMovementResponse
                .builder()
                .inbound(sumQuantityByType(transactions, "INBOUND"))
                .outbound(sumQuantityByType(transactions, "OUTBOUND"))
                .transfers(sumCompletedTransferQuantity(transfers))
                .adjustments(sumQuantityByType(transactions, "ADJUSTMENT"))
                .build();
    }

    @Override
    public List<WarehousePerformanceResponse> getWarehousePerformance() {
        return warehouseClient
                .getWarehouses()
                .stream()
                .map(warehouse -> {
                    int capacity = warehouse.getCapacity() == null ? 0 : warehouse.getCapacity();
                    int utilization = warehouse.getCurrentUtilization() == null ? 0 : warehouse.getCurrentUtilization();
                    double utilizationPercentage = capacity == 0 ? 0.0 : (utilization * 100.0) / capacity;

                    return WarehousePerformanceResponse
                            .builder()
                            .warehouseName(warehouse.getName())
                            .capacity(capacity)
                            .currentUtilization(utilization)
                            .availableCapacity(Math.max(capacity - utilization, 0))
                            .utilizationPercentage(utilizationPercentage)
                            .status(warehouse.getStatus())
                            .build();
                })
                .toList();
    }

    @Override
    public List<ForecastResponse> getForecast() {
        Map<Long, ProductDto> productsById = productClient
                .getProducts()
                .stream()
                .collect(Collectors.toMap(ProductDto::getId, Function.identity()));

        Map<Long, Integer> outboundByProduct = inventoryClient
                .getTransactions()
                .stream()
                .filter(tx -> "OUTBOUND".equalsIgnoreCase(tx.getType()))
                .collect(Collectors.groupingBy(
                        InventoryTransactionDto::getProductId,
                        Collectors.summingInt(tx -> tx.getQuantity() == null ? 0 : tx.getQuantity())
                ));

        return outboundByProduct.entrySet()
                .stream()
                .map(entry -> {
                    ProductDto product = productsById.get(entry.getKey());
                    if (product == null) {
                        return null;
                    }

                    double averageDailyDemand = entry.getValue() / 30.0;

                    return ForecastResponse.builder()
                            .productName(product.getName())
                            .averageDailyDemand(averageDailyDemand)
                            .forecastFor30Days(entry.getValue())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private int sumQuantityByType(List<InventoryTransactionDto> transactions, String type) {
        return transactions.stream()
                .filter(tx -> type.equalsIgnoreCase(tx.getType()))
                .mapToInt(tx -> tx.getQuantity() == null ? 0 : tx.getQuantity())
                .sum();
    }

    private int sumCompletedTransferQuantity(List<StockTransferDto> transfers) {
        return transfers.stream()
                .filter(transfer -> "COMPLETED".equalsIgnoreCase(transfer.getStatus()))
                .mapToInt(transfer -> transfer.getQuantity() == null ? 0 : transfer.getQuantity())
                .sum();
    }
}
