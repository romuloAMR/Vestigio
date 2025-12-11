package com.example.vestigioapi.application.trivia.game.dto;

import com.example.vestigioapi.framework.engine.GameMoveDTO;

public record TriviaAnswerResponseDTO(
    Long questionId,
    Integer selectedAnswerIndex,
    Boolean isCorrect,
    Integer correctAnswerIndex,
    Integer pointsEarned,
    Integer totalScore,
    String explanation
) implements GameMoveDTO {}
