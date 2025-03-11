package com.fawry.store_api.exception;

import com.fawry.store_api.enums.ErrorCode;

public class InsufficientStockException extends BaseException {
    public InsufficientStockException(Long productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product ID: %d. Requested: %d, Available: %d",
                        productId, requestedQuantity, availableQuantity),
                ErrorCode.INSUFFICIENT_STOCK);
    }
}