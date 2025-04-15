package com.fawry.store_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record StoreDTO(
        Long id,
        @NotBlank(message = "Store name is mandatory")
        @Size(min = 2, max = 100, message = "Store name must be between 2 and 100 characters")
        String name,
        @NotBlank(message = "Store address is mandatory")
        @Size(min = 5, max = 255, message = "Store address must be between 5 and 255 characters")
        String address,
        Integer productCount,
        Integer totalStockItems,
        @Pattern(
                regexp = "^(https?|ftp)://[\\w.-]+(?:\\.[\\w\\.-]+)+[/#?]?.*$",
                message = "Invalid image URL format"
        )
        String imageUrl
) {}