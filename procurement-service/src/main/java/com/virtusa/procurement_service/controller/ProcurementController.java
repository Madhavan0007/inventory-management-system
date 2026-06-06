package com.virtusa.procurement_service.controller;

import com.virtusa.procurement_service.dto.PurchaseRequestDto;
import com.virtusa.procurement_service.entity.PurchaseRequest;
import com.virtusa.procurement_service.service.ProcurementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/procurement")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @PostMapping("/request")
    public PurchaseRequest createRequest(@RequestBody PurchaseRequestDto dto) {
        return procurementService.createRequest(dto);
    }

    @GetMapping
    public List<PurchaseRequest> getAllRequests() {
        return procurementService.getAllRequests();
    }

    @GetMapping("/{id}")
    public PurchaseRequest getRequestById(@PathVariable Long id) {
        return procurementService.getRequestById(id);
    }

    @PutMapping("/approve/{id}")
    public PurchaseRequest approveRequest(@PathVariable Long id) {
        return procurementService.approveRequest(id);
    }

    @PutMapping("/reject/{id}")
    public PurchaseRequest rejectRequest(@PathVariable Long id) {
        return procurementService.rejectRequest(id);
    }

    @PutMapping("/delivered/{id}")
    public PurchaseRequest markAsDelivered(@PathVariable Long id) {
        return procurementService.markAsDelivered(id);
    }
}