package com.fawry.store_api.service.impl;

import com.fawry.store_api.dto.ProductConsumptionDTO;
import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.entity.ProductConsumption;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.exception.InsufficientStockException;
import com.fawry.store_api.mapper.ProductConsumptionMapper;
import com.fawry.store_api.repository.ProductConsumptionRepository;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.ProductConsumptionService;
import com.fawry.store_api.service.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductConsumptionServiceImpl implements ProductConsumptionService {
    private final ProductConsumptionRepository consumptionRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final ProductConsumptionMapper consumptionMapper;
    private final WebClientService webClientService;

    @Override
    public ProductConsumptionDTO createProductConsumption(ProductConsumptionDTO consumptionDTO) {
        validateStoreExists(consumptionDTO.storeId());
        ProductResponseDTO product = validateAndGetProduct(consumptionDTO.productId());
        Stock stock = validateAndGetStock(consumptionDTO.storeId(), consumptionDTO.productId());

        validateStockQuantity(stock, consumptionDTO.consumptionQuantity());

        try {

            updateStockQuantity(stock, consumptionDTO.consumptionQuantity());


            ProductConsumptionDTO enrichedDTO = enrichConsumptionWithProductInfo(consumptionDTO, product);
            ProductConsumption consumption = consumptionMapper.toEntity(enrichedDTO);
            ProductConsumption savedConsumption = consumptionRepository.save(consumption);

            return consumptionMapper.toDTO(savedConsumption);
        } catch (Exception e) {
            log.error("Error processing product consumption: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process product consumption", e);
        }
    }

    @Override
    public ProductConsumptionDTO getProductConsumptionById(Long id) {
        return consumptionRepository.findById(id)
                .map(consumptionMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("ProductConsumption", id));
    }

    @Override
    public List<ProductConsumptionDTO> getProductConsumptionsByStoreId(Long storeId) {
        validateStoreExists(storeId);
        List<ProductConsumption> consumptions = consumptionRepository.findByStoreId(storeId);
        return consumptionMapper.toDTOList(consumptions);
    }

    @Override
    public void deleteProductConsumption(Long id) {
        ProductConsumption consumption = findConsumptionById(id);
        try {
            consumptionRepository.delete(consumption);
            log.info("Product consumption with ID {} successfully deleted", id);
        } catch (Exception e) {
            log.error("Error deleting product consumption with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete product consumption", e);
        }
    }


    private void validateStoreExists(Long storeId) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store", storeId));
    }

    private ProductResponseDTO validateAndGetProduct(Long productId) {
        return webClientService.getProducts(Set.of(productId))
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product", productId));
    }

    private Stock validateAndGetStock(Long storeId, Long productId) {
        return stockRepository.findByStoreIdAndProductId(storeId, productId)
                .orElseThrow(() -> new EntityNotFoundException("Stock",
                        String.format("Store ID: %d, Product ID: %d", storeId, productId)));
    }

    private void validateStockQuantity(Stock stock, Integer requestedQuantity) {
        if (stock.getStockAvailableQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    stock.getProductId(),
                    requestedQuantity,
                    stock.getStockAvailableQuantity()
            );
        }
    }

    private void updateStockQuantity(Stock stock, Integer consumptionQuantity) {
        stock.setStockAvailableQuantity(
                stock.getStockAvailableQuantity() - consumptionQuantity
        );
        stockRepository.save(stock);
    }

    private ProductConsumptionDTO enrichConsumptionWithProductInfo(
            ProductConsumptionDTO originalDTO,
            ProductResponseDTO product) {
        return ProductConsumptionDTO.builder()
                .consumptionId(originalDTO.consumptionId())
                .productId(originalDTO.productId())
                .productName(product.name())
                .storeId(originalDTO.storeId())
                .consumptionQuantity(originalDTO.consumptionQuantity())
                .consumptionDate(Instant.now())
                .productPrice(BigDecimal.valueOf(product.price()))
                .build();
    }

    private ProductConsumption findConsumptionById(Long id) {
        return consumptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ProductConsumption", id));
    }
}