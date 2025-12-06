package com.example.vestigioapi.application.game.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.application.game.move.AnswerType;

public record MoveDTO(
    Long id,
    String question,
    AnswerType answer,
    String authorName,
    LocalDateTime createdAt
) {}
