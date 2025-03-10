package com.fawry.store_api.service;

import com.fawry.store_api.dto.StockDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.exception.EntityAlreadyExistsException;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.mapper.StockMapper;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final StockMapper stockMapper;

    @Override
    public StockDTO createStock(StockDTO stockDTO) {

        storeRepository.findById(stockDTO.getStoreId())
                .orElseThrow(() -> {
                    log.error("Store not found for stock creation: {}", stockDTO.getStoreId());
                    return new EntityNotFoundException("Store", stockDTO.getStoreId());
                });


        stockRepository.findByStoreIdAndProductId(stockDTO.getStoreId(), stockDTO.getProductId())
                .ifPresent(existingStock -> {
                    log.error("Stock already exists for store {} and product {}",
                            stockDTO.getStoreId(), stockDTO.getProductId());
                    throw new EntityAlreadyExistsException("Stock",
                            String.format("Store ID: %d, Product ID: %d",
                                    stockDTO.getStoreId(), stockDTO.getProductId())
                    );
                });

        try {
            Stock stock = stockMapper.toEntity(stockDTO);
            Stock savedStock = stockRepository.save(stock);
            log.info("Stock created successfully for store {} and product {}",
                    savedStock.getStore().getId(), savedStock.getProductId());
            return stockMapper.toDTO(savedStock);
        } catch (Exception e) {
            log.error("Error creating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create stock", e);
        }
    }

    @Override
    public StockDTO getStockById(Long id) {
        return stockRepository.findById(id)
                .map(stock -> {
                    log.info("Stock found with ID: {}", id);
                    return stockMapper.toDTO(stock);
                })
                .orElseThrow(() -> {
                    log.error("Stock not found with ID: {}", id);
                    return new EntityNotFoundException("Stock", id);
                });
    }

    @Override
    public List<StockDTO> getStockByStoreId(Long storeId) {

        storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found for stock retrieval: {}", storeId);
                    return new EntityNotFoundException("Store", storeId);
                });

        try {
            List<Stock> stocks = stockRepository.findByStoreId(storeId);
            log.info("Retrieved {} stocks for store {}", stocks.size(), storeId);
            return stockMapper.toDTOList(stocks);
        } catch (Exception e) {
            log.error("Error retrieving stocks for store {}: {}", storeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve stocks", e);
        }
    }

    @Override
    public StockDTO updateStock(StockDTO stockDTO) {

        Stock existingStock = stockRepository.findById(stockDTO.getId())
                .orElseThrow(() -> {
                    log.error("Stock not found with ID for update: {}", stockDTO.getId());
                    return new EntityNotFoundException("Stock", stockDTO.getId());
                });

        try {
            existingStock.setStockAvailableQuantity(stockDTO.getStockAvailableQuantity());

            Stock updatedStock = stockRepository.save(existingStock);
            log.info("Stock updated successfully with ID: {}", updatedStock.getId());
            return stockMapper.toDTO(updatedStock);
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock", e);
        }
    }

    @Override
    public void deleteStock(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Stock not found with ID for deletion: {}", id);
                    return new EntityNotFoundException("Stock", id);
                });

        try {
            stockRepository.delete(stock);
            log.info("Stock deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete stock", e);
        }
    }

    @Override
    public StockDTO updateStockQuantity(Long storeId, Long productId, Integer quantity) {

        if (quantity < 0) {
            log.error("Attempted to set negative stock quantity: {}", quantity);
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Stock stock = stockRepository.findByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> {
                    log.error("Stock not found for store {} and product {}", storeId, productId);
                    return new EntityNotFoundException("Stock",
                            String.format("Store ID: %d, Product ID: %d", storeId, productId)
                    );
                });

        try {
            stock.setStockAvailableQuantity(quantity);
            Stock updatedStock = stockRepository.save(stock);
            log.info("Stock quantity updated for store {} and product {} to {}",
                    storeId, productId, quantity);
            return stockMapper.toDTO(updatedStock);
        } catch (Exception e) {
            log.error("Error updating stock quantity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock quantity", e);
        }
    }
}