package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductResponseDTO;

import java.util.List;
import java.util.Set;

public interface WebClientService {
    List<ProductResponseDTO> getProducts(Set<Long> productIds);

    void checkIfProductsExist(Long productIds);
}
