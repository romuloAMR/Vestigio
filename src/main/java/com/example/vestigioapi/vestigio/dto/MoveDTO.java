package com.example.vestigioapi.vestigio.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.vestigio.model.AnswerType;

public record MoveDTO(
    Long id,
    String question,
    AnswerType answer,
    String authorName,
    LocalDateTime createdAt
) {}
