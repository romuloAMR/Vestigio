package com.example.vestigioapi.application.vestigio.game.move;

import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum AnswerType {
    YES,
    NO,
    INDIFFERENT;

    @JsonCreator
    public static AnswerType fromString(String value) {
        if (value == null) return null;
        String v = value.trim().toUpperCase(Locale.ROOT);
        return switch (v) {
            case "YES", "SIM" -> YES;
            case "NO", "NAO", "NÃO" -> NO;
            case "INDIFFERENT", "INDIFERENTE", "NEUTRAL", "NEUTRO" -> INDIFFERENT;
            default -> throw new BusinessRuleException(ErrorMessages.UNKNOWN_ANSWER_TYPE, value);
        };
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
