package com.fawry.store_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record StockDTO(
        Long id,
        @NotNull(message = "Product ID is mandatory")
        Long productId,
        String productName,
        BigDecimal productPrice,
        String productDescription,
        String productImage,
        @NotNull(message = "Store ID is mandatory")
        Long storeId,
        @NotNull(message = "Stock available quantity is mandatory")
        @Min(value = 0, message = "Stock available quantity must be at least 0")
        Integer stockAvailableQuantity,
        Instant stockLastUpdated
) {}