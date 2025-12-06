package com.example.vestigioapi.application.game.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.application.game.move.AnswerType;
import com.example.vestigioapi.framework.engine.GameMoveDTO;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;

public record MoveResponseDTO(
    Long id,
    String question,
    AnswerType answer,
    PlayerDTO author,
    LocalDateTime createdAt
) implements GameMoveDTO{}
