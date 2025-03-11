package com.fawry.store_api.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    RESOURCE_ALREADY_EXISTS(HttpStatus.CONFLICT, "Resource already exists"),


    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for requested quantity"),
    NEGATIVE_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "Stock quantity cannot be negative"),


    CONSUMPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Product consumption not found"),
    CONSUMPTION_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create product consumption"),


    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed"),


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
    OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Operation failed");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}