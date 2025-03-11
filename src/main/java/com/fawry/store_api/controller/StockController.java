package com.fawry.store_api.controller;

import com.fawry.store_api.dto.StockDTO;
import com.fawry.store_api.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockDTO> createStock(
            @Valid @RequestBody StockDTO stockDTO
    ) {
        StockDTO createdStock = stockService.createStock(stockDTO);
        return new ResponseEntity<>(createdStock, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDTO> getStockById(
            @PathVariable Long id
    ) {
        StockDTO stock = stockService.getStockById(id);
        return ResponseEntity.ok(stock);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<StockDTO>> getStockByStoreId(
            @PathVariable Long storeId
    ) {
        List<StockDTO> stocks = stockService.getStockByStoreId(storeId);
        return ResponseEntity.ok(stocks);
    }

    @PutMapping
    public ResponseEntity<StockDTO> updateStock(
            @Valid @RequestBody StockDTO stockDTO
    ) {
        StockDTO updatedStock = stockService.updateStock(stockDTO);
        return ResponseEntity.ok(updatedStock);
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<StockDTO> updateStockQuantity(
            @RequestParam Long storeId,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) {
        StockDTO updatedStock = stockService.updateStockQuantity(storeId, productId, quantity);
        return ResponseEntity.ok(updatedStock);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(
            @PathVariable Long id
    ) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}