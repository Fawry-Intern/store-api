package com.fawry.store_api.service;

import com.fawry.store_api.dto.StockDTO;

import java.util.List;

public interface StockService {
    StockDTO createStock(StockDTO stockDTO);

    StockDTO getStockById(Long id);

    List<StockDTO> getStockByStoreId(Long storeId);

    StockDTO updateStock(StockDTO stockDTO);

    void deleteStock(Long id);

    StockDTO updateStockQuantity(Long storeId, Long productId, Integer quantity);
}