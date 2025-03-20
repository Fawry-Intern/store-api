package com.fawry.store_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductConsumptionDTO(
        Long consumptionId,
        @NotNull(message = "Product ID is mandatory")
        Long productId,
        String productName,
        @NotNull(message = "Store ID is mandatory")
        Long storeId,
        @NotNull(message = "Consumption quantity is mandatory")
        @Min(value = 1, message = "Consumption quantity must be at least 1")
        Integer consumptionQuantity,
        Instant consumptionDate,
        BigDecimal productPrice
) {}