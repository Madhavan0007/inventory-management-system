package com.virtusa.reporting_service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionDto {

    private Long productId;

    private Long warehouseId;

    private String type;

    private Integer quantity;

    private LocalDateTime createdAt;
}