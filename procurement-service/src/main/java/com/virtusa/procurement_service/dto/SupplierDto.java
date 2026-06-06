package com.virtusa.procurement_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {

    private String supplierName;

    private String email;

    private String phoneNumber;

    private String address;
}