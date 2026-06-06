package com.virtusa.reporting_service.client;


import com.virtusa.reporting_service.dto.WarehouseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "warehouse-service",
        url = "${services.warehouse.url}"
)
public interface WarehouseClient {

    @GetMapping("/warehouses")
    List<WarehouseDto> getWarehouses();
}