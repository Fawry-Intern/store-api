package com.fawry.store_api.service.impl;

import com.fawry.kafka.dto.OrderItemDTO;
import com.fawry.kafka.events.OrderCanceledEventDTO;
import com.fawry.kafka.events.OrderCreatedEventDTO;
import com.fawry.kafka.events.StoreCreatedEventDTO;
import com.fawry.kafka.producers.StoreCancellationPublisher;
import com.fawry.kafka.producers.StoreUpdatedPublisher;
import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.entity.InventoryReservation;
import com.fawry.store_api.entity.Stock;
import com.fawry.store_api.entity.Store;
import com.fawry.store_api.enums.ReservationStatus;
import com.fawry.store_api.exception.EntityAlreadyExistsException;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.exception.InsufficientInventoryException;
import com.fawry.store_api.mapper.StoreMapper;
import com.fawry.store_api.repository.InventoryReservationRepository;
import com.fawry.store_api.repository.StockRepository;
import com.fawry.store_api.repository.StoreRepository;
import com.fawry.store_api.service.StoreService;
import com.fawry.store_api.service.WebClientService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fawry.store_api.enums.ReservationStatus.CANCELED;
import static com.fawry.store_api.enums.ReservationStatus.RESERVED;

@Service
@Transactional
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;
    private final StockRepository stockRepository;
    private final WebClientService webClientService;
    private final InventoryReservationRepository inventoryReservationRepository;
    private final StoreCancellationPublisher storeCancellationPublisher;
    private final StoreUpdatedPublisher storeUpdatedPublisher;

    @Value("${custom.merchant.email}")
    private String merchantEmail;
    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository,
                            StoreMapper storeMapper,
                            StockRepository stockRepository,
                            WebClientService webClientService,
                            InventoryReservationRepository inventoryReservationRepository,
                            StoreCancellationPublisher storeCancellationPublisher,
                            StoreUpdatedPublisher storeUpdatedPublisher) {
        this.storeRepository = storeRepository;
        this.storeMapper = storeMapper;
        this.stockRepository = stockRepository;
        this.webClientService = webClientService;
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.storeCancellationPublisher = storeCancellationPublisher;
        this.storeUpdatedPublisher = storeUpdatedPublisher;
    }

    @Override
    @Transactional
    @KafkaListener(topics = "order-events", groupId = "store_order_id", topicPartitions = {
            @TopicPartition(topic = "order-events", partitions = {"0", "1", "2", "4"}
            )})
    public void reserveStore(OrderCreatedEventDTO order) {
        Long orderId = order.getOrderId();
        log.info("Consume order created event successfully {}", order);
        List<OrderItemDTO> orderItems = order.getOrderItems();

        List<InventoryReservation> reservations = new ArrayList<>();
        try {
            for (OrderItemDTO orderItem : orderItems) {
                Long storeId = orderItem.getStoreId();
                Long productId = orderItem.getProductId();
                int quantity = orderItem.getQuantity();

                Stock stock = getStock(storeId, productId);
                int availableQuantity = getAvailableInventory(stock);

                if (availableQuantity < quantity) {
                    String customerEmail = order.getCustomerEmail();
                    OrderCanceledEventDTO orderCanceledEventDTO = new OrderCanceledEventDTO(orderId, "Not enough inventory for product " + productId, customerEmail);
                    cancelReservation(orderCanceledEventDTO);
                    throw new InsufficientInventoryException("Not enough inventory for product " + productId);
                }

                stock.setStockAvailableQuantity(stock.getStockAvailableQuantity() - quantity);
                stockRepository.save(stock);

                var inventoryReservation = new InventoryReservation();
                inventoryReservation.setProductId(productId);
                inventoryReservation.setOrderId(orderId);
                inventoryReservation.setReservedQuantity(quantity);
                inventoryReservation.setStatus(RESERVED);
                inventoryReservationRepository.save(inventoryReservation);
                reservations.add(inventoryReservation);
            }

            StoreCreatedEventDTO storeCreatedEventDTO = new StoreCreatedEventDTO(
                    orderId, order.getUserId(),
                    RESERVED.name(), order.getCustomerEmail(),
                    order.getCustomerName(), order.getCustomerContact(),
                    order.getAddressDetails(),
                    order.getPaymentAmount(),
                    order.getPaymentMethod(),
                    merchantEmail
            );
            storeUpdatedPublisher.publishStoreUpdatedEvent(storeCreatedEventDTO);

        } catch (Exception e) {
            log.error("Failed to reserve stock for orderId: {}. Error: {}", orderId, e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @KafkaListener(topics = "payment-canceled-events", groupId = "store_payment_id")
    public void cancelReservation(OrderCanceledEventDTO orderCanceledEventDTO) {
        long orderId = orderCanceledEventDTO.getOrderId();
        List<InventoryReservation> inventoryReservations = inventoryReservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("InventoryReservation not found with id", orderId));

        for (InventoryReservation inventoryReservation : inventoryReservations) {
            Long productId = inventoryReservation.getProductId();
            int reserveQuantity = inventoryReservation.getReservedQuantity();
            Stock stock = stockRepository.findByProductId(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Stock not found with product id", productId));
            stock.setStockAvailableQuantity(stock.getStockAvailableQuantity() + reserveQuantity);
            inventoryReservation.setStatus(CANCELED);
        }

        storeCancellationPublisher.publishOrderCanceledEvent(orderCanceledEventDTO);
    }

    private Stock getStock(long storeId, long productId){
        return stockRepository.findByStoreIdAndProductId(storeId, productId).
                orElseThrow(() -> new RuntimeException("Stock not found for product " + productId));
    }

    private int getAvailableInventory(Stock stock) {
        return stock.getStockAvailableQuantity();
    }

    @Override
    public StoreDTO createStore(StoreDTO storeDTO) {
        storeRepository.findByName(storeDTO.name())
                .ifPresent(store -> {
                    throw new EntityAlreadyExistsException("Store", storeDTO.name());
                });

        Store store = storeMapper.toEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return storeMapper.toDTO(savedStore, 0, 0);
    }

    @Override
    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store", id));

        List<Stock> stocks = stockRepository.findByStoreId(id);
        int totalStockItems = stocks.size();
        int productCount = calculateTotalAvailableQuantity(stocks);

        return storeMapper.toDTO(store, productCount, totalStockItems);
    }

    @Override
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        List<Integer> productCounts = stores.stream()
                .map(store -> calculateTotalAvailableQuantity(
                        stockRepository.findByStoreId(store.getId())))
                .collect(Collectors.toList());

        List<Integer> totalStockItems = stores.stream()
                .map(store -> stockRepository.findByStoreId(store.getId()).size())
                .collect(Collectors.toList());

        return storeMapper.toDTOList(stores, productCounts, totalStockItems);
    }

    @Override
    public StoreDTO updateStore(StoreDTO storeDTO) {
        Store existingStore = storeRepository.findById(storeDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("Store", storeDTO.id()));

        if (!existingStore.getName().equals(storeDTO.name())) {
            storeRepository.findByName(storeDTO.name())
                    .ifPresent(store -> {
                        throw new EntityAlreadyExistsException("Store", storeDTO.name());
                    });
        }

        existingStore.setName(storeDTO.name());
        existingStore.setAddress(storeDTO.address());
        Store updatedStore = storeRepository.save(existingStore);

        List<Stock> stocks = stockRepository.findByStoreId(updatedStore.getId());
        int totalStockItems = stocks.size();
        int productCount = calculateTotalAvailableQuantity(stocks);

        return storeMapper.toDTO(updatedStore, productCount, totalStockItems);
    }

    @Override
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Store", id));

        List<Stock> stocks = stockRepository.findByStoreId(id);
        if (!stocks.isEmpty()) {
            stockRepository.deleteAll(stocks);
        }

        storeRepository.delete(store);
    }

    @Override
    public Page<ProductResponseDTO> getStoreProducts(Long storeId, int page, int size) {
        storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store", storeId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Stock> stocks =stockRepository.findByStoreId(storeId, pageable);


        Set<Long> productIds = stocks.stream()
                .map(Stock::getProductId)
                .collect(Collectors.toSet());

        List<ProductResponseDTO> products = webClientService.getProducts(productIds);

        return new PageImpl<>(products, pageable, stocks.getTotalElements());
    }

    private int calculateTotalAvailableQuantity(List<Stock> stocks) {
        return stocks.stream()
                .mapToInt(Stock::getStockAvailableQuantity)
                .sum();
    }
}