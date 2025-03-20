package com.fawry.store_api.dto;

import lombok.Builder;

@Builder
public record ProductResponseDTO(
        Long id,
        String name,
        Double price,
        String description,
        String imageUrl
) {}