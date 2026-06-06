package com.virtusa.reporting_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {

    private Long id;

    private String name;

    private String location;

    private Integer capacity;

    private Integer currentUtilization;

    private String status;
}
