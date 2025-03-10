package com.fawry.store_api.repository;

import com.fawry.store_api.entity.ProductConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductConsumptionRepository extends JpaRepository<ProductConsumption, Long> {
    List<ProductConsumption> findByStoreId(Long storeId);
    List<ProductConsumption> findByStoreIdAndProductId(Long storeId, Long productId);
}
