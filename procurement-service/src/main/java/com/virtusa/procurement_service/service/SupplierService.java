package com.virtusa.procurement_service.service;

import com.virtusa.procurement_service.entity.Supplier;

import java.util.List;

public interface SupplierService {

    Supplier addSupplier(Supplier supplier);

    List<Supplier> getAllSuppliers();

    Supplier getSupplierById(Long id);
}