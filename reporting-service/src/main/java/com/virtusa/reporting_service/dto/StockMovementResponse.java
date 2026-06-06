package com.virtusa.reporting_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockMovementResponse {

    private Integer inbound;

    private Integer outbound;

    private Integer transfers;

    private Integer adjustments;
}