package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductConsumptionDTO;
import com.fawry.store_api.entity.ProductConsumption;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.exception.InsufficientStockException;
import com.fawry.store_api.mapper.ProductConsumptionMapper;
import com.fawry.store_api.repository.ProductConsumptionRepository;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductConsumptionServiceImpl implements ProductConsumptionService {
    private final ProductConsumptionRepository consumptionRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final ProductConsumptionMapper consumptionMapper;

    @Override
    public ProductConsumptionDTO createProductConsumption(ProductConsumptionDTO consumptionDTO) {

        Store store = storeRepository.findById(consumptionDTO.getStoreId())
                .orElseThrow(() -> {
                    log.error("Store not found for consumption: {}", consumptionDTO.getStoreId());
                    return new EntityNotFoundException("Store", consumptionDTO.getStoreId());
                });

        Stock stock = stockRepository.findByStoreIdAndProductId(store.getId(), consumptionDTO.getProductId())
                .orElseThrow(() -> {
                    log.error("Stock not found for store {} and product {}",
                            store.getId(), consumptionDTO.getProductId());
                    return new EntityNotFoundException("Stock",
                            String.format("Store ID: %d, Product ID: %d",
                                    store.getId(), consumptionDTO.getProductId())
                    );
                });


        if (stock.getStockAvailableQuantity() < consumptionDTO.getConsumptionQuantity()) {
            log.error("Insufficient stock for product {}. Requested: {}, Available: {}",
                    consumptionDTO.getProductId(),
                    consumptionDTO.getConsumptionQuantity(),
                    stock.getStockAvailableQuantity());
            throw new InsufficientStockException(
                    consumptionDTO.getProductId(),
                    consumptionDTO.getConsumptionQuantity(),
                    stock.getStockAvailableQuantity()
            );
        }

        try {

            stock.setStockAvailableQuantity(
                    stock.getStockAvailableQuantity() - consumptionDTO.getConsumptionQuantity()
            );
            stockRepository.save(stock);


            List<ProductConsumption> existingConsumptions = consumptionRepository
                    .findByStoreIdAndProductId(store.getId(), consumptionDTO.getProductId());

            ProductConsumption savedConsumption;
            if (!existingConsumptions.isEmpty()) {

                ProductConsumption consumption = existingConsumptions.get(0);
                consumption.setConsumptionQuantity(
                        consumption.getConsumptionQuantity() + consumptionDTO.getConsumptionQuantity()
                );
                savedConsumption = consumptionRepository.save(consumption);
            } else {

                ProductConsumption consumption = consumptionMapper.toEntity(consumptionDTO);
                savedConsumption = consumptionRepository.save(consumption);
            }

            log.info("Product consumption processed successfully for store {} and product {}",
                    store.getId(), consumptionDTO.getProductId());
            return consumptionMapper.toDTO(savedConsumption);
        } catch (Exception e) {
            log.error("Error processing product consumption: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process product consumption", e);
        }
    }


    @Override
    public ProductConsumptionDTO getProductConsumptionById(Long id) {
        return consumptionRepository.findById(id)
                .map(consumption -> {
                    log.info("Product consumption found with ID: {}", id);
                    return consumptionMapper.toDTO(consumption);
                })
                .orElseThrow(() -> {
                    log.error("Product consumption not found with ID: {}", id);
                    return new EntityNotFoundException("ProductConsumption", id);
                });
    }

    @Override
    public List<ProductConsumptionDTO> getProductConsumptionsByStoreId(Long storeId) {
        // Verify store exists
        storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    log.error("Store not found for consumption retrieval: {}", storeId);
                    return new EntityNotFoundException("Store", storeId);
                });

        try {
            List<ProductConsumption> consumptions = consumptionRepository.findByStoreId(storeId);
            log.info("Retrieved {} product consumptions for store {}", consumptions.size(), storeId);
            return consumptionMapper.toDTOList(consumptions);
        } catch (Exception e) {
            log.error("Error retrieving product consumptions for store {}: {}", storeId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve product consumptions", e);
        }
    }

    @Override
    public void deleteProductConsumption(Long id) {

        ProductConsumption consumption = consumptionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product consumption not found with ID for deletion: {}", id);
                    return new EntityNotFoundException("ProductConsumption", id);
                });

        try {
            consumptionRepository.delete(consumption);
            log.info("Product consumption deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting product consumption: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete product consumption", e);
        }
    }
}