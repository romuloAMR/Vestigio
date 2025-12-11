package com.example.vestigioapi.application.hangman.game.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.framework.engine.GameMoveDTO;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;

public record HangmanMoveResponse(
    Long moveId,
    Character guessedLetter,
    boolean isCorrect,
    PlayerDTO author,
    LocalDateTime createdAt
) implements GameMoveDTO {
}
