package com.fawry.store_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDTO {
    private Long id;

    @NotBlank(message = "Store name is mandatory")
    @Size(min = 2, max = 100, message = "Store name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Store address is mandatory")
    @Size(min = 5, max = 255, message = "Store address must be between 5 and 255 characters")
    private String address;

    //TODO retrieving products data from product-api
}