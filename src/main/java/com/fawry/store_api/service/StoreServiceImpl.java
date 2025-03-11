package com.fawry.store_api.service;

import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.exception.EntityAlreadyExistsException;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.mapper.StoreMapper;
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
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

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
                    return storeMapper.toDTO(store);
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
            return storeMapper.toDTOList(stores);
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
            return storeMapper.toDTO(updatedStore);
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
            storeRepository.delete(store);
            log.info("Store deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting store: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete store", e);
        }
    }
}