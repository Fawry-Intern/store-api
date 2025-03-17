package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.exception.EntityAlreadyExistsException;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.mapper.StoreMapper;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final StockRepository stockRepository;
    private final WebClientService webClientService;

    @Override
    public StoreDTO createStore(StoreDTO storeDTO) {
        storeRepository.findByName(storeDTO.getName())
                .ifPresent(existingStore -> {
                    log.error("Attempt to create store with existing name: {}", storeDTO.getName());
                    throw new EntityAlreadyExistsException("Store", storeDTO.getName());
                });

        try {
            Store store = storeMapper.toEntity(storeDTO);
            Store savedStore = storeRepository.save(store);
            log.info("Store created successfully with ID: {}", savedStore.getId());
            return storeMapper.toDTO(savedStore);
        } catch (Exception e) {
            log.error("Error creating store: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create store", e);
        }
    }

    @Override
    public StoreDTO getStoreById(Long id) {
        return storeRepository.findById(id)
                .map(store -> {
                    log.info("Store found with ID: {}", id);
                    StoreDTO storeDTO = storeMapper.toDTO(store);


                    try {
                        List<Stock> stocks = stockRepository.findByStoreId(id);
                        if (!stocks.isEmpty()) {
                            Set<Long> productIds = stocks.stream()
                                    .map(Stock::getProductId)
                                    .collect(Collectors.toSet());

                            List<ProductResponseDTO> products = webClientService.getProducts(productIds);


                            storeDTO.setProductCount(products.size());
                            storeDTO.setTotalStockItems(stocks.size());
                        }
                    } catch (Exception e) {

                        log.warn("Could not enrich store with product information: {}", e.getMessage());
                    }

                    return storeDTO;
                })
                .orElseThrow(() -> {
                    log.error("Store not found with ID: {}", id);
                    return new EntityNotFoundException("Store", id);
                });
    }

    @Override
    public List<StoreDTO> getAllStores() {
        try {
            List<Store> stores = storeRepository.findAll();
            log.info("Retrieved {} stores", stores.size());

            return stores.stream()
                    .map(store -> {
                        StoreDTO storeDTO = storeMapper.toDTO(store);
                        enrichStoreWithProductInfo(storeDTO);
                        return storeDTO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving stores: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve stores", e);
        }
    }


    @Override
    public StoreDTO updateStore(StoreDTO storeDTO) {
        if (storeDTO.getId() == null) {
            log.error("Store ID is required for update");
            throw new IllegalArgumentException("Store ID must be provided");
        }


        Store existingStore = storeRepository.findById(storeDTO.getId())
                .orElseThrow(() -> {
                    log.error("Store not found with ID for update: {}", storeDTO.getId());
                    return new EntityNotFoundException("Store", storeDTO.getId());
                });


        if (!existingStore.getName().equals(storeDTO.getName())) {
            storeRepository.findByName(storeDTO.getName())
                    .ifPresent(store -> {
                        log.error("Attempt to update store with existing name: {}", storeDTO.getName());
                        throw new EntityAlreadyExistsException("Store", storeDTO.getName());
                    });
        }

        try {

            existingStore.setName(storeDTO.getName());
            existingStore.setAddress(storeDTO.getAddress());


            Store updatedStore = storeRepository.save(existingStore);
            log.info("Store updated successfully with ID: {}", updatedStore.getId());


            StoreDTO updatedStoreDTO = storeMapper.toDTO(updatedStore);
            enrichStoreWithProductInfo(updatedStoreDTO);

            return updatedStoreDTO;
        } catch (Exception e) {
            log.error("Error updating store: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update store", e);
        }
    }

    @Override
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Store not found with ID for deletion: {}", id);
                    return new EntityNotFoundException("Store", id);
                });

        try {

            List<Stock> stocks = stockRepository.findByStoreId(id);
            if (!stocks.isEmpty()) {
                log.warn("Deleting store with ID: {} which has {} associated stocks", id, stocks.size());
                stockRepository.deleteAll(stocks);
            }

            storeRepository.delete(store);
            log.info("Store deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting store: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete store", e);
        }
    }


    public List<ProductResponseDTO> getStoreProducts(Long storeId) {

        storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found with ID: {}", storeId);
                    return new EntityNotFoundException("Store", storeId);
                });

        try {

            List<Stock> stocks = stockRepository.findByStoreId(storeId);

            if (stocks.isEmpty()) {
                log.info("No products found in store with ID: {}", storeId);
                return List.of();
            }


            Set<Long> productIds = stocks.stream()
                    .map(Stock::getProductId)
                    .collect(Collectors.toSet());


            List<ProductResponseDTO> products = webClientService.getProducts(productIds);
            log.info("Retrieved {} products for store with ID: {}", products.size(), storeId);

            return products;
        } catch (Exception e) {
            log.error("Error retrieving products for store with ID {}: {}", storeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve store products", e);
        }
    }


    private void enrichStoreWithProductInfo(StoreDTO storeDTO) {
        try {
            List<Stock> stocks = stockRepository.findByStoreId(storeDTO.getId());
            if (!stocks.isEmpty()) {
                Set<Long> productIds = stocks.stream()
                        .map(Stock::getProductId)
                        .collect(Collectors.toSet());

                List<ProductResponseDTO> products = webClientService.getProducts(productIds);

                storeDTO.setProductCount(products.size());
                storeDTO.setTotalStockItems(stocks.size());
            } else {
                storeDTO.setProductCount(0);
                storeDTO.setTotalStockItems(0);
            }
        } catch (Exception e) {
            log.warn("Could not enrich store with product information: {}", e.getMessage());
            storeDTO.setProductCount(0);
            storeDTO.setTotalStockItems(0);
        }
    }
}