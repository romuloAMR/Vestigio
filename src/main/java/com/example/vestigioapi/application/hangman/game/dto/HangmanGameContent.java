package com.example.vestigioapi.application.hangman.game.dto;

import com.example.vestigioapi.framework.engine.GameContent;

public record HangmanGameContent(
    String maskedWord,
    String guessedLetters,
    int remainingAttempts,
    int maxAttempts,
    boolean isGameOver,
    boolean isVictory
) implements GameContent {
}
