package com.fawry.store_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {
    private Long id;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Store ID is mandatory")
    private Long storeId;

    @NotNull(message = "Stock available quantity is mandatory")
    @Min(value = 0, message = "Stock available quantity must be at least 0")
    private Integer stockAvailableQuantity;

    private Instant stockLastUpdated;
}
