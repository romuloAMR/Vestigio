package com.example.vestigioapi.vestigio.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.core.session.dto.PlayerDTO;
import com.example.vestigioapi.vestigio.model.AnswerType;

public record MoveResponseDTO(
    Long id,
    String question,
    AnswerType answer,
    PlayerDTO author,
    LocalDateTime createdAt
) {}
