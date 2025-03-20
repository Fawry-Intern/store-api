package com.fawry.store_api.mapper;

import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StockDTO;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.WebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockMapper {
    private final StoreRepository storeRepository;
    private final WebClientService webClientService;

    public StockMapper(StoreRepository storeRepository, WebClientService webClientService) {
        this.storeRepository = storeRepository;
        this.webClientService = webClientService;
    }

    public StockDTO toDTO(Stock stock) {
        if (stock == null) return null;

        ProductResponseDTO product = null;
        try {
            product = webClientService.getProducts(Set.of(stock.getProductId()))
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Could not fetch product details for stock: {}", e.getMessage());
        }

        return StockDTO.builder()
                .id(stock.getId())
                .productId(stock.getProductId())
                .productName(product != null ? product.name() : null)
                .productPrice(product != null ? BigDecimal.valueOf(product.price()) : null)
                .productDescription(product != null ? product.description() : null)
                .productImage(product != null ? product.imageUrl() : null)
                .storeId(stock.getStore().getId())
                .stockAvailableQuantity(stock.getStockAvailableQuantity())
                .stockLastUpdated(stock.getStockLastUpdated())
                .build();
    }

    public Stock toEntity(StockDTO dto) {
        if (dto == null) return null;

        Store store = storeRepository.findById(dto.storeId())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        return Stock.builder()
                .id(dto.id())
                .productId(dto.productId())
                .store(store)
                .stockAvailableQuantity(dto.stockAvailableQuantity())
                .stockLastUpdated(Instant.now())
                .build();
    }

    public List<StockDTO> toDTOList(List<Stock> stocks) {
        return stocks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
