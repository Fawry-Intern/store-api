package com.fawry.store_api.service.impl;

import com.fawry.store_api.dto.StockDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.mapper.StockMapper;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.StockService;
import com.fawry.store_api.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final StockMapper stockMapper;
    private final WebClientService webClientService;

    @Override
    public StockDTO createStock(StockDTO stockDTO) {
        validateStoreExists(stockDTO.storeId());
        validateProductExists(stockDTO.productId());

        try {
            Stock stock = getOrCreateStock(stockDTO);
            Stock savedStock = stockRepository.save(stock);
            return stockMapper.toDTO(savedStock);
        } catch (Exception e) {
            log.error("Error creating/updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create/update stock", e);
        }
    }

    @Override
    public StockDTO getStockById(Long id) {
        Stock stock = findStockById(id);
        return stockMapper.toDTO(stock);
    }

    @Override
    public List<StockDTO> getStockByStoreId(Long storeId) {
        validateStoreExists(storeId);
        List<Stock> stocks = stockRepository.findByStoreId(storeId);
        return stockMapper.toDTOList(stocks);
    }

    @Override
    public StockDTO updateStock(StockDTO stockDTO) {
        Stock existingStock = findStockById(stockDTO.id());
        validateProductExists(stockDTO.productId());

        try {
            updateStockDetails(existingStock, stockDTO);
            Stock savedStock = stockRepository.save(existingStock);
            return stockMapper.toDTO(savedStock);
        } catch (Exception e) {
            log.error("Error updating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock", e);
        }
    }

    @Override
    public StockDTO updateStockQuantity(Long storeId, Long productId, Integer quantity) {
        validateQuantity(quantity);
        Stock stock = findStockByStoreAndProduct(storeId, productId);

        try {
            stock.setStockAvailableQuantity(quantity);
            stock.setStockLastUpdated(Instant.now());
            Stock savedStock = stockRepository.save(stock);
            return stockMapper.toDTO(savedStock);
        } catch (Exception e) {
            log.error("Error updating stock quantity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock quantity", e);
        }
    }

    @Override
    public void deleteStock(Long id) {
        Stock stock = findStockById(id);
        try {
            stockRepository.delete(stock);
            log.info("Stock with ID {} successfully deleted", id);
        } catch (Exception e) {
            log.error("Error deleting stock with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete stock", e);
        }
    }


    private void validateStoreExists(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store", storeId));
    }

    private void validateProductExists(Long productId) {
        webClientService.getProducts(Set.of(productId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    private Stock getOrCreateStock(StockDTO stockDTO) {
        Optional<Stock> existingStock = stockRepository.findByStoreIdAndProductId(
                stockDTO.storeId(),
                stockDTO.productId()
        );

        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setStockAvailableQuantity(
                    stock.getStockAvailableQuantity() + stockDTO.stockAvailableQuantity()
            );
            stock.setStockLastUpdated(Instant.now());
            return stock;
        }

        return stockMapper.toEntity(stockDTO);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }

    private Stock findStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock", id));
    }

    private Stock findStockByStoreAndProduct(Long storeId, Long productId) {
        return stockRepository.findByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Stock",
                        String.format("Store ID: %d, Product ID: %d", storeId, productId)));
    }

    private void updateStockDetails(Stock stock, StockDTO stockDTO) {
        stock.setStockAvailableQuantity(stockDTO.stockAvailableQuantity());
        stock.setStockLastUpdated(Instant.now());
    }
}