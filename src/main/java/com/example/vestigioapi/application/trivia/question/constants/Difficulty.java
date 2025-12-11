package com.example.vestigioapi.application.trivia.question.constants;

public enum Difficulty {
    EASY("Fácil", 1),
    MEDIUM("Médio", 2),
    HARD("Difícil", 3);

    private final String displayName;
    private final Integer level;

    Difficulty(String displayName, Integer level) {
        this.displayName = displayName;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getLevel() {
        return level;
    }
}
