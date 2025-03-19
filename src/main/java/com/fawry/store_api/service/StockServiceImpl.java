package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductResponseDTO;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
        storeRepository.findById(stockDTO.getStoreId())
                .orElseThrow(() -> {
                    log.error("Store not found for stock creation: {}", stockDTO.getStoreId());
                    return new EntityNotFoundException("Store", stockDTO.getStoreId());
                });

        try {
            webClientService.checkIfProductsExist(stockDTO.getProductId());
            log.info("Product validation successful for ID: {}", stockDTO.getProductId());
        } catch (ResponseStatusException e) {
            log.error("Product validation failed for ID {}: {}", stockDTO.getProductId(), e.getMessage());
            throw new EntityNotFoundException("Product", stockDTO.getProductId());
        }

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
            log.info("Stock created successfully with ID: {} and last updated: {}",
                    savedStock.getId(), savedStock.getStockLastUpdated());

            enrichStockWithProductInfo(stockDTO);

            stockDTO.setId(savedStock.getId());
            stockDTO.setStockLastUpdated(savedStock.getStockLastUpdated());

            return stockDTO;
        } catch (Exception e) {
            log.error("Error creating stock: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create stock", e);
        }
    }


    @Override
    public StockDTO getStockById(Long id) {
        StockDTO stockDTO = stockRepository.findById(id)
                .map(stock -> {
                    log.info("Stock found with ID: {}", id);
                    return stockMapper.toDTO(stock);
                })
                .orElseThrow(() -> {
                    log.error("Stock not found with ID: {}", id);
                    return new EntityNotFoundException("Stock", id);
                });

        enrichStockWithProductInfo(stockDTO);

        return stockDTO;
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


            List<StockDTO> stockDTOs = stockMapper.toDTOList(stocks);
            enrichStocksWithProductInfo(stockDTOs);

            return stockDTOs;
        } catch (Exception e) {
            log.error("Error retrieving stocks for store {}: {}", storeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve stocks", e);
        }
    }

    private void enrichStocksWithProductInfo(List<StockDTO> stockDTOs) {
        try {
            Set<Long> productIds = stockDTOs.stream()
                    .map(StockDTO::getProductId)
                    .collect(java.util.stream.Collectors.toSet());

            if (!productIds.isEmpty()) {
                List<ProductResponseDTO> products = webClientService.getProducts(productIds);

                java.util.Map<Long, ProductResponseDTO> productMap = products.stream()
                        .collect(java.util.stream.Collectors.toMap(
                                ProductResponseDTO::getId,
                                product -> product
                        ));

                stockDTOs.forEach(stockDTO -> {
                    ProductResponseDTO product = productMap.get(stockDTO.getProductId());
                    if (product != null) {
                        stockDTO.setProductName(product.getName());
                        stockDTO.setProductPrice(product.getPrice());
                    }
                });

                log.info("Successfully enriched {} stocks with product information", stockDTOs.size());
            }
        } catch (Exception e) {
            log.warn("Unable to enrich stocks with product information: {}", e.getMessage());
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

            StockDTO updatedStockDTO = stockMapper.toDTO(updatedStock);

            enrichStockWithProductInfo(updatedStockDTO);

            return updatedStockDTO;
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


        try {
            webClientService.checkIfProductsExist(productId);
            log.info("Product validation successful for ID: {}", productId);
        } catch (ResponseStatusException e) {
            log.error("Product validation failed for ID {}: {}", productId, e.getMessage());
            throw new EntityNotFoundException("Product", productId);
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


            StockDTO updatedStockDTO = stockMapper.toDTO(updatedStock);


            enrichStockWithProductInfo(updatedStockDTO);

            return updatedStockDTO;
        } catch (Exception e) {
            log.error("Error updating stock quantity: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update stock quantity", e);
        }
    }

    private void enrichStockWithProductInfo(StockDTO stockDTO) {
        try {
            List<ProductResponseDTO> products = webClientService.getProducts(Set.of(stockDTO.getProductId()));
            if (!products.isEmpty()) {
                ProductResponseDTO product = products.get(0);
                stockDTO.setProductName(product.getName());
                stockDTO.setProductPrice(product.getPrice());
                log.info("Enriched stockDTO with product info: {}", stockDTO);
            } else {
                log.warn("Product not found for ID: {}", stockDTO.getProductId());
            }
        } catch (Exception e) {
            log.warn("Unable to enrich stock with product information: {}", e.getMessage());
        }
    }

}