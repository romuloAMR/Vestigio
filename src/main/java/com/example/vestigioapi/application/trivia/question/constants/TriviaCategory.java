package com.example.vestigioapi.application.trivia.question.constants;

public enum TriviaCategory {
    SCIENCE("Ciência"),
    HISTORY("História"),
    GEOGRAPHY("Geografia"),
    TECHNOLOGY("Tecnologia"),
    SPORTS("Esportes"),
    ENTERTAINMENT("Entretenimento"),
    LITERATURE("Literatura"),
    GENERAL_KNOWLEDGE("Conhecimento Geral");

    private final String displayName;

    TriviaCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
