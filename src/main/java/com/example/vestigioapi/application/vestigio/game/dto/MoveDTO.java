package com.example.vestigioapi.application.vestigio.game.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.application.vestigio.game.move.AnswerType;

public record MoveDTO(
    Long id,
    String question,
    AnswerType answer,
    String authorName,
    LocalDateTime createdAt
) {}
