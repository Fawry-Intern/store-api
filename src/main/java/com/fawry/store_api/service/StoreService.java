package com.fawry.store_api.service;

import com.fawry.kafka.events.OrderCanceledEventDTO;
import com.fawry.kafka.events.OrderCreatedEventDTO;
import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;

import java.util.List;

public interface StoreService {
    void reserveStore(OrderCreatedEventDTO order);

    void cancelReservation(OrderCanceledEventDTO orderCanceledEventDTO);

    StoreDTO createStore(StoreDTO storeDTO);

    StoreDTO getStoreById(Long id);

    List<StoreDTO> getAllStores();

    StoreDTO updateStore(StoreDTO storeDTO);

    void deleteStore(Long id);

    List<ProductResponseDTO> getStoreProducts(Long storeId, int page, int size);

}
