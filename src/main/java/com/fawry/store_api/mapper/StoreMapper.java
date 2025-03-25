package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.Store;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class StoreMapper {
    public StoreDTO toDTO(Store store, int productCount, int totalStockItems) {
        if (store == null) return null;
        return StoreDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .productCount(productCount)
                .totalStockItems(totalStockItems)
                .build();
    }

    public Store toEntity(StoreDTO dto) {
        if (dto == null) return null;
        return Store.builder()
                .id(dto.id())
                .name(dto.name())
                .address(dto.address())
                .build();
    }

    public List<StoreDTO> toDTOList(List<Store> stores, List<Integer> productCounts, List<Integer> totalStockItems) {
        return IntStream.range(0, stores.size())
                .mapToObj(i -> toDTO(stores.get(i), productCounts.get(i), totalStockItems.get(i)))
                .collect(Collectors.toList());
    }
}