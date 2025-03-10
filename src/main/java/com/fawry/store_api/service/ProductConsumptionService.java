package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductConsumptionDTO;

import java.util.List;

public interface ProductConsumptionService {
    ProductConsumptionDTO createProductConsumption(ProductConsumptionDTO consumptionDTO);

    ProductConsumptionDTO getProductConsumptionById(Long id);

    List<ProductConsumptionDTO> getProductConsumptionsByStoreId(Long storeId);

    void deleteProductConsumption(Long id);

}