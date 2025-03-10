package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.Store;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreMapper {
    public StoreDTO toDTO(Store store) {
        if (store == null) return null;
        return StoreDTO.builder()
                .id(store.getId())
                .name(store.getName())
                .address(store.getAddress())
                .build();
    }

    public Store toEntity(StoreDTO storeDTO) {
        if (storeDTO == null) return null;
        return Store.builder()
                .id(storeDTO.getId())
                .name(storeDTO.getName())
                .address(storeDTO.getAddress())
                .build();
    }

    public List<StoreDTO> toDTOList(List<Store> stores) {
        return stores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
