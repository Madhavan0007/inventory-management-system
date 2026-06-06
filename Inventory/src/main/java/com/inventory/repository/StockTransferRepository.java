package com.inventory.repository;

import com.inventory.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    List<StockTransfer> findByProductId(Long productId);
    List<StockTransfer> findBySourceWarehouseIdOrDestinationWarehouseId(Long src, Long dst);
}
