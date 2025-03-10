package com.fawry.store_api.exception;

import com.fawry.store_api.enums.ErrorCode;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s not found with ID: %d", entityName, id), ErrorCode.RESOURCE_NOT_FOUND);
    }

    public EntityNotFoundException(String entityName, String identifier) {
        super(String.format("%s not found with identifier: %s", entityName, identifier), ErrorCode.RESOURCE_NOT_FOUND);
    }
}