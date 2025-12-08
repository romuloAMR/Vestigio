package com.example.vestigioapi.application.trivia.game.dto;

public record AnswerQuestionRequestDTO(
    Long questionId,
    Integer selectedAnswerIndex
) {}
