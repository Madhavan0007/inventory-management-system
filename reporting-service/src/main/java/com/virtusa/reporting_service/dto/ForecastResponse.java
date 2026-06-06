package com.virtusa.reporting_service.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForecastResponse {

    private String productName;

    private Double averageDailyDemand;

    private Integer forecastFor30Days;
}