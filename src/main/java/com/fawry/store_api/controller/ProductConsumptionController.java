package com.fawry.store_api.controller;

import com.fawry.store_api.dto.ProductConsumptionDTO;
import com.fawry.store_api.service.ProductConsumptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consumptions")
@RequiredArgsConstructor
public class ProductConsumptionController {
    private final ProductConsumptionService consumptionService;

    @PostMapping
    public ResponseEntity<ProductConsumptionDTO> createProductConsumption(
            @Valid @RequestBody ProductConsumptionDTO consumptionDTO
    ) {
        ProductConsumptionDTO createdConsumption = consumptionService.createProductConsumption(consumptionDTO);
        return new ResponseEntity<>(createdConsumption, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductConsumptionDTO> getProductConsumptionById(
            @PathVariable Long id
    ) {
        ProductConsumptionDTO consumption = consumptionService.getProductConsumptionById(id);
        return ResponseEntity.ok(consumption);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductConsumptionDTO>> getProductConsumptionsByStoreId(
            @PathVariable Long storeId
    ) {
        List<ProductConsumptionDTO> consumptions = consumptionService.getProductConsumptionsByStoreId(storeId);
        return ResponseEntity.ok(consumptions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductConsumption(
            @PathVariable Long id
    ) {
        consumptionService.deleteProductConsumption(id);
        return ResponseEntity.noContent().build();
    }
}