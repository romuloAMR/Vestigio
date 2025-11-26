package com.example.vestigioapi.application.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.application.model.move.AnswerType;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;

public record MoveResponseDTO(
    Long id,
    String question,
    AnswerType answer,
    PlayerDTO author,
    LocalDateTime createdAt
) {}
