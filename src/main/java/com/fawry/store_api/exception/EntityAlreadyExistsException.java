package com.fawry.store_api.exception;

import com.fawry.store_api.enums.ErrorCode;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException(String entityName, String identifier) {
        super(String.format("%s already exists with identifier: %s", entityName, identifier), ErrorCode.RESOURCE_ALREADY_EXISTS);
    }
}