package com.virtusa.procurement_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {

    private Long productId;

    private Long warehouseId;

    private String productName;

    private Integer currentStock;

    private Integer requestedQuantity;

    private Long supplierId;
}
