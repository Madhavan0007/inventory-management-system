package com.virtusa.procurement_service.controller;

import com.virtusa.procurement_service.entity.Supplier;
import com.virtusa.procurement_service.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public Supplier addSupplier(@RequestBody Supplier supplier) {

        return supplierService.addSupplier(supplier);
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {

        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable Long id) {

        return supplierService.getSupplierById(id);
    }
}