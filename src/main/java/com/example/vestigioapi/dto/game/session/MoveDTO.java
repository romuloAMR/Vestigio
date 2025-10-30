package com.example.vestigioapi.dto.game.session;

import java.time.LocalDateTime;

import com.example.vestigioapi.model.game.move.AnswerType;

public record MoveDTO(
    Long id,
    String question,
    AnswerType answer,
    String authorName,
    LocalDateTime createdAt
) {}
