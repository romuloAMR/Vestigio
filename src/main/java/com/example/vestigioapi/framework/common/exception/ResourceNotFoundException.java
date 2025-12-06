package com.example.vestigioapi.framework.common.exception;

public class ResourceNotFoundException extends GameFrameworkException {
    public ResourceNotFoundException(String messageKey, Object... args){
        super(messageKey, args);
    }
}
