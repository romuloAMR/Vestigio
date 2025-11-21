package com.example.vestigioapi.core.common.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(){
        super();
    }
    public BusinessRuleException(String message){
        super(message);
    }
}
