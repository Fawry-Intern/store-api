package com.fawry.store_api.service;

import com.fawry.store_api.dto.ProductResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WebClientServiceImpl implements WebClientService {

    private final WebClient webClient;

    public WebClientServiceImpl(@Value("${product.api.base-url:http://localhost:7071}") String productApiBaseUrl) {
        this.webClient = WebClient.create(productApiBaseUrl);
    }

    @Override
    public List<ProductResponseDTO> getProducts(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }

        String ids = productIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/products")
                            .queryParam("ids", ids)
                            .build())
                    .retrieve()
                    .bodyToFlux(ProductResponseDTO.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.valueOf(e.getStatusCode().value()),
                    "Error fetching products: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void checkIfProductsExist(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        try {
            webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product with ID " + productId + " not found"
                );
            }
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error checking product existence: " + e.getMessage(),
                    e
            );
        }
    }
}