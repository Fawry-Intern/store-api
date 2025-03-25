package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.ProductConsumptionDTO;
import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.entity.ProductConsumption;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.WebClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProductConsumptionMapper {
    private final StoreRepository storeRepository;
    private final WebClientService webClientService;

    public ProductConsumptionMapper(StoreRepository storeRepository, WebClientService webClientService) {
        this.storeRepository = storeRepository;
        this.webClientService = webClientService;

    }

    public ProductConsumptionDTO toDTO(ProductConsumption consumption) {
        if (consumption == null) return null;

        ProductResponseDTO product = null;
        try {
            product = webClientService.getProducts(Set.of(consumption.getProductId()))
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
           log.error("Error getting product: {}", e.getMessage(), e);
        }

        return ProductConsumptionDTO.builder()
                .consumptionId(consumption.getConsumptionId())
                .productId(consumption.getProductId())
                .productName(product != null ? product.name() : null)
                .storeId(consumption.getStore().getId())
                .consumptionQuantity(consumption.getConsumptionQuantity())
                .consumptionDate(consumption.getConsumptionDate())
                .productPrice(consumption.getProductPrice())
                .build();
    }

    public ProductConsumption toEntity(ProductConsumptionDTO dto) {
        if (dto == null) return null;

        Store store = storeRepository.findById(dto.storeId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return ProductConsumption.builder()
                .consumptionId(dto.consumptionId())
                .productId(dto.productId())
                .store(store)
                .consumptionQuantity(dto.consumptionQuantity())
                .productPrice(dto.productPrice())
                .consumptionDate(Instant.now())
                .build();
    }

    public List<ProductConsumptionDTO> toDTOList(List<ProductConsumption> consumptions) {
        return consumptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}