package com.virtusa.reporting_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDto {

    private Long productId;

    private Long warehouseId;

    private Integer quantity;

    private Integer minimumStock;
}