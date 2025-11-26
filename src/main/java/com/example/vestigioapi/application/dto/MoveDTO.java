package com.example.vestigioapi.application.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.application.model.move.AnswerType;

public record MoveDTO(
    Long id,
    String question,
    AnswerType answer,
    String authorName,
    LocalDateTime createdAt
) {}
