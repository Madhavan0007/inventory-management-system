package com.virtusa.procurement_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long warehouseId;

    private String productName;

    private Integer currentStock;

    private Integer requestedQuantity;

    private LocalDate requestDate;

    @Enumerated(EnumType.STRING)
    private ProcurementStatus status;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
