package com.example.vestigioapi.application.trivia.question.dto;

import com.example.vestigioapi.application.trivia.question.constants.Difficulty;
import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;

import java.util.List;

public record QuestionCreateDTO(
    String text,
    List<String> options,
    Integer correctAnswerIndex,
    TriviaCategory category,
    Difficulty difficulty,
    String explanation
) {}
