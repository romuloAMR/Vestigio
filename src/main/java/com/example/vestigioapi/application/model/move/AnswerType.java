package com.example.vestigioapi.application.model.move;

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
            default -> throw new IllegalArgumentException("Unknown AnswerType: " + value);
        };
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
