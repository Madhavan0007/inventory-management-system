package com.virtusa.reporting_service.client;


import com.virtusa.reporting_service.dto.InventoryDto;
import com.virtusa.reporting_service.dto.InventoryTransactionDto;
import com.virtusa.reporting_service.dto.StockTransferDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        url = "${services.inventory.url}"
)
public interface InventoryClient {

    @GetMapping("/inventory")
    List<InventoryDto> getInventory();

    @GetMapping("/inventory/transactions/all")
    List<InventoryTransactionDto> getTransactions();

    @GetMapping("/inventory/transfers")
    List<StockTransferDto> getTransfers();
}
