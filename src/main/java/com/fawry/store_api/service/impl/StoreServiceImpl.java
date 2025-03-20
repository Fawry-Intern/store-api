package com.fawry.store_api.service.impl;

import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.exception.EntityAlreadyExistsException;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.mapper.StoreMapper;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.StoreService;
import com.fawry.store_api.service.WebClientService;
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
        storeRepository.findByName(storeDTO.name())
                .ifPresent(store -> {
                    throw new EntityAlreadyExistsException("Store", storeDTO.name());
                });

        Store store = storeMapper.toEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return storeMapper.toDTO(savedStore, 0, 0);
    }

    @Override
    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store", id));

        List<Stock> stocks = stockRepository.findByStoreId(id);
        int totalStockItems = stocks.size();
        int productCount = calculateTotalAvailableQuantity(stocks);

        return storeMapper.toDTO(store, productCount, totalStockItems);
    }

    @Override
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        List<Integer> productCounts = stores.stream()
                .map(store -> calculateTotalAvailableQuantity(
                        stockRepository.findByStoreId(store.getId())))
                .collect(Collectors.toList());

        List<Integer> totalStockItems = stores.stream()
                .map(store -> stockRepository.findByStoreId(store.getId()).size())
                .collect(Collectors.toList());

        return storeMapper.toDTOList(stores, productCounts, totalStockItems);
    }

    @Override
    public StoreDTO updateStore(StoreDTO storeDTO) {
        Store existingStore = storeRepository.findById(storeDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("Store", storeDTO.id()));

        if (!existingStore.getName().equals(storeDTO.name())) {
            storeRepository.findByName(storeDTO.name())
                    .ifPresent(store -> {
                        throw new EntityAlreadyExistsException("Store", storeDTO.name());
                    });
        }

        existingStore.setName(storeDTO.name());
        existingStore.setAddress(storeDTO.address());
        Store updatedStore = storeRepository.save(existingStore);

        List<Stock> stocks = stockRepository.findByStoreId(updatedStore.getId());
        int totalStockItems = stocks.size();
        int productCount = calculateTotalAvailableQuantity(stocks);

        return storeMapper.toDTO(updatedStore, productCount, totalStockItems);
    }

    @Override
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store", id));

        List<Stock> stocks = stockRepository.findByStoreId(id);
        if (!stocks.isEmpty()) {
            stockRepository.deleteAll(stocks);
        }

        storeRepository.delete(store);
    }

    @Override
    public List<ProductResponseDTO> getStoreProducts(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store", storeId));

        List<Stock> stocks = stockRepository.findByStoreId(storeId);
        if (stocks.isEmpty()) {
            return List.of();
        }

        Set<Long> productIds = stocks.stream()
                .map(Stock::getProductId)
                .collect(Collectors.toSet());

        return webClientService.getProducts(productIds);
    }

    private int calculateTotalAvailableQuantity(List<Stock> stocks) {
        return stocks.stream()
                .mapToInt(Stock::getStockAvailableQuantity)
                .sum();
    }
}