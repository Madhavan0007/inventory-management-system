package com.virtusa.procurement_service.service;

import com.virtusa.procurement_service.dto.PurchaseRequestDto;
import com.virtusa.procurement_service.entity.PurchaseRequest;

import java.util.List;

public interface ProcurementService {

    PurchaseRequest createRequest(PurchaseRequestDto dto);

    List<PurchaseRequest> getAllRequests();

    PurchaseRequest getRequestById(Long id);

    PurchaseRequest approveRequest(Long id);

    PurchaseRequest rejectRequest(Long id);

    PurchaseRequest markAsDelivered(Long id);
}