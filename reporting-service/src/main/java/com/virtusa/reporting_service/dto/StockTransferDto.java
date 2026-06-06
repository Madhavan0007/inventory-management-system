package com.virtusa.reporting_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferDto {

    private Long productId;

    private Long sourceWarehouseId;

    private Long destinationWarehouseId;

    private Integer quantity;

    private String status;
}
