package com.virtusa.procurement_service.service;

import com.virtusa.procurement_service.client.InventoryClient;
import com.virtusa.procurement_service.dto.PurchaseRequestDto;
import com.virtusa.procurement_service.entity.*;
import com.virtusa.procurement_service.exceptions.InvalidQuantityException;
import com.virtusa.procurement_service.exceptions.PurchaseRequestNotFoundException;
import com.virtusa.procurement_service.exceptions.RequestAlreadyProcessedException;
import com.virtusa.procurement_service.exceptions.SupplierNotFoundException;
import com.virtusa.procurement_service.repository.PurchaseRequestRepository;
import com.virtusa.procurement_service.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProcurementServiceImpl implements ProcurementService {

    private final PurchaseRequestRepository requestRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryClient inventoryClient;

    public ProcurementServiceImpl(PurchaseRequestRepository requestRepository,
                                  SupplierRepository supplierRepository,
                                  InventoryClient inventoryClient) {
        this.requestRepository = requestRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryClient = inventoryClient;
    }

    @Override
    public PurchaseRequest createRequest(PurchaseRequestDto dto) {

        if (dto == null || dto.getProductId() == null || dto.getSupplierId() == null) {
            throw new InvalidQuantityException(
                    "Product id and supplier id are required");
        }
        if (dto.getRequestedQuantity() == null || dto.getRequestedQuantity() <= 0) {
            throw new InvalidQuantityException(
                    "Requested quantity must be greater than zero");
        }
        if (dto.getWarehouseId() == null) {
            throw new InvalidQuantityException("Warehouse id is required");
        }

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() ->
                        new SupplierNotFoundException("Supplier not found"));

        PurchaseRequest request = PurchaseRequest.builder()
                .productId(dto.getProductId())
                .warehouseId(dto.getWarehouseId())
                .productName(dto.getProductName())
                .currentStock(dto.getCurrentStock())
                .requestedQuantity(dto.getRequestedQuantity())
                .requestDate(LocalDate.now())
                .status(ProcurementStatus.PENDING)
                .supplier(supplier)
                .build();

        return requestRepository.save(request);
    }

    @Override
    public List<PurchaseRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    public PurchaseRequest getRequestById(Long id) {

        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new PurchaseRequestNotFoundException(
                                "Request not found with id: " + id));
    }

    @Override
    public PurchaseRequest approveRequest(Long id) {

        PurchaseRequest request = getRequestById(id);

        if (request.getStatus() == ProcurementStatus.APPROVED) {
            throw new RequestAlreadyProcessedException(
                    "Request already approved");
        }

        request.setStatus(ProcurementStatus.APPROVED);

        return requestRepository.save(request);
    }

    @Override
    public PurchaseRequest rejectRequest(Long id) {

        PurchaseRequest request = getRequestById(id);

        if (request.getStatus() == ProcurementStatus.REJECTED) {
            throw new RequestAlreadyProcessedException(
                    "Request already rejected");
        }

        request.setStatus(ProcurementStatus.REJECTED);

        return requestRepository.save(request);
    }

    @Override
    public PurchaseRequest markAsDelivered(Long id) {

        PurchaseRequest request = getRequestById(id);

        if (request.getStatus() == ProcurementStatus.DELIVERED) {
            throw new RequestAlreadyProcessedException(
                    "Request already delivered");
        }
        if (request.getStatus() != ProcurementStatus.APPROVED) {
            throw new RequestAlreadyProcessedException(
                    "Only approved requests can be marked as delivered");
        }

        inventoryClient.inbound(
                request.getProductId(),
                request.getWarehouseId(),
                request.getRequestedQuantity()
        );

        request.setStatus(ProcurementStatus.DELIVERED);

        return requestRepository.save(request);
    }
}
