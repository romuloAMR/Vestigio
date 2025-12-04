package com.example.vestigioapi.framework.common.exception;

public class BusinessRuleException extends GameFrameworkException {
    public BusinessRuleException(String messageKey, Object... args){
        super(messageKey, args);
    }
}
