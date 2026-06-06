package com.virtusa.warehouse_service.dto;

import com.virtusa.warehouse_service.enums.WarehouseStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponse {

    private Long id;
    private String name;
    private String location;
    private Integer capacity;
    private Integer currentUtilization;
    private WarehouseStatus status;
}