package com.example.vestigioapi.framework.common.exception;

public class ForbiddenActionException extends GameFrameworkException {
    public ForbiddenActionException(String messageKey, Object... args){
        super(messageKey, args);
    }
}
