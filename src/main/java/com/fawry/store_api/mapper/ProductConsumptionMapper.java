package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.ProductConsumptionDTO;
import com.fawry.store_api.entity.ProductConsumption;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.repository.StoreRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductConsumptionMapper {
    private final StoreRepository storeRepository;

    public ProductConsumptionMapper(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public ProductConsumptionDTO toDTO(ProductConsumption consumption) {
        if (consumption == null) return null;
        return ProductConsumptionDTO.builder()
                .consumptionId(consumption.getConsumptionId())
                .productId(consumption.getProductId())
                .storeId(consumption.getStore().getId())
                .consumptionQuantity(consumption.getConsumptionQuantity())
                .productPrice(consumption.getProductPrice())
                .consumptionDate(consumption.getConsumptionDate())
                .build();
    }

    public ProductConsumption toEntity(ProductConsumptionDTO consumptionDTO) {
        if (consumptionDTO == null) return null;
        Store store = storeRepository.findById(consumptionDTO.getStoreId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return ProductConsumption.builder()
                .consumptionId(consumptionDTO.getConsumptionId())
                .productId(consumptionDTO.getProductId())
                .store(store)
                .consumptionQuantity(consumptionDTO.getConsumptionQuantity())
                .productPrice(consumptionDTO.getProductPrice())
                .build();
    }

    public List<ProductConsumptionDTO> toDTOList(List<ProductConsumption> consumptions) {
        return consumptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}