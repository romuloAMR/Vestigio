package com.example.vestigioapi.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(){
        super();
    }
    public BusinessRuleException(String message){
        super(message);
    }
}
