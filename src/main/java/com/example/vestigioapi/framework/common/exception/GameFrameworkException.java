package com.example.vestigioapi.framework.common.exception;

import lombok.Getter;

@Getter
public class GameFrameworkException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;

    public GameFrameworkException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
}
