package com.fawry.store_api.controller;

import com.fawry.store_api.dto.ProductResponseDTO;
import com.fawry.store_api.dto.StoreDTO;
import com.fawry.store_api.exception.EntityNotFoundException;
import com.fawry.store_api.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreDTO> createStore(
            @Valid @RequestBody StoreDTO storeDTO
    ) {
        StoreDTO createdStore = storeService.createStore(storeDTO);
        return new ResponseEntity<>(createdStore, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDTO> getStoreById(
            @PathVariable Long id
    ) {
        StoreDTO store = storeService.getStoreById(id);
        return ResponseEntity.ok(store);
    }

    @GetMapping
    public ResponseEntity<List<StoreDTO>> getAllStores() {
        List<StoreDTO> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }


    @GetMapping("/{storeId}/products")
    public ResponseEntity<List<ProductResponseDTO>> getStoreProducts(@PathVariable Long storeId,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        try {
            List<ProductResponseDTO> products = storeService.getStoreProducts(storeId, page, size);
            return ResponseEntity.ok(products);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PutMapping
    public ResponseEntity<StoreDTO> updateStore(
            @Valid @RequestBody StoreDTO storeDTO
    ) {
        StoreDTO updatedStore = storeService.updateStore(storeDTO);
        return ResponseEntity.ok(updatedStore);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable Long id
    ) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}