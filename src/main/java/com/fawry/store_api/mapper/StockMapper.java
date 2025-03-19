package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.StockDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.repository.StoreRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMapper {
    private final StoreRepository storeRepository;

    public StockMapper(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public StockDTO toDTO(Stock stock) {
        if (stock == null) return null;
        return StockDTO.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .storeId(stock.getStore().getId())
                .stockAvailableQuantity(stock.getStockAvailableQuantity())
                .stockLastUpdated(stock.getStockLastUpdated())
                .build();
    }

    public Stock toEntity(StockDTO stockDTO) {
        if (stockDTO == null) return null;
        Store store = storeRepository.findById(stockDTO.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return Stock.builder()
                .productId(stockDTO.getProductId())
                .store(store)
                .stockAvailableQuantity(stockDTO.getStockAvailableQuantity())
                .stockLastUpdated(Instant.now())
                .build();
    }

    public List<StockDTO> toDTOList(List<Stock> stocks) {
        return stocks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}