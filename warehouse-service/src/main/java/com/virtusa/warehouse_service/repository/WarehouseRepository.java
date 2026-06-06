package com.virtusa.warehouse_service.repository;

import com.virtusa.warehouse_service.entity.Warehouse;
import com.virtusa.warehouse_service.enums.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByIdAndStatus(Long id, WarehouseStatus status);

    List<Warehouse> findByStatus(WarehouseStatus status);
}