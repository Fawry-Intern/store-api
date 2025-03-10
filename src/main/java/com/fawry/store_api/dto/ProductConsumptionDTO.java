package com.fawry.store_api.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductConsumptionDTO {
    private Long consumptionId;

    @NotNull(message = "Product ID is mandatory")
    private Long productId;

    @NotNull(message = "Store ID is mandatory")
    private Long storeId;

    @NotNull(message = "Consumption quantity is mandatory")
    @Min(value = 1, message = "Consumption quantity must be at least 1")
    private Integer consumptionQuantity;

    private Instant consumptionDate;

    @NotNull(message = "Product price is mandatory")
    @Positive(message = "Product price must be positive")
    @Digits(integer = 10, fraction = 2, message = "Product price must have up to 10 integer digits and 2 decimal digits")
    private BigDecimal productPrice;


}