package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;

import java.util.List;

public interface StoreService {
    StoreDTO createStore(StoreDTO storeDTO);

    StoreDTO getStoreById(Long id);

    List<StoreDTO> getAllStores();

    StoreDTO updateStore(StoreDTO storeDTO);

    void deleteStore(Long id);

    List<ProductResponseDTO> getStoreProducts(Long storeId);

}
