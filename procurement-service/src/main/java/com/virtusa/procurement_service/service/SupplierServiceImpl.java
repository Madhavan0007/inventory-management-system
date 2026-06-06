package com.virtusa.procurement_service.service;

import com.virtusa.procurement_service.entity.Supplier;
import com.virtusa.procurement_service.exceptions.SupplierNotFoundException;
import com.virtusa.procurement_service.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Supplier addSupplier(Supplier supplier) {

        return supplierRepository.save(supplier);
    }

    @Override
    public List<Supplier> getAllSuppliers() {

        return supplierRepository.findAll();
    }

    @Override
    public Supplier getSupplierById(Long id) {

        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new SupplierNotFoundException(
                                "Supplier not found with id: " + id));
    }
}