package com.virtusa.reporting_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryValuationResponse {

    private String productName;

    private Integer quantity;

    private Double unitPrice;

    private Double totalValue;
}