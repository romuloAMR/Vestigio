package com.example.vestigioapi.application.trivia.game.dto;

import com.example.vestigioapi.application.trivia.question.constants.Difficulty;
import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;
import com.example.vestigioapi.framework.engine.GameContent;

import java.util.List;

public record QuestionResponseDTO(
    Long id,
    String text,
    List<String> options,
    TriviaCategory category,
    Difficulty difficulty,
    Integer currentScore,
    Integer totalQuestions,
    Integer currentQuestionIndex
) implements GameContent {}
