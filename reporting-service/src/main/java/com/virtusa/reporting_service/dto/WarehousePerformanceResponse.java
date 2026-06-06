package com.virtusa.reporting_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WarehousePerformanceResponse {

    private String warehouseName;

    private Integer capacity;

    private Integer currentUtilization;

    private Integer availableCapacity;

    private Double utilizationPercentage;

    private String status;
}
