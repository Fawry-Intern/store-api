package com.fawry.store_api.repository;

import com.fawry.store_api.entity.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Page<Stock> findByStoreId(Long storeId, Pageable pageable);
    List<Stock> findByStoreId(Long storeId);
    Optional<Stock> findByStoreIdAndProductId(Long storeId, Long productId);

}
