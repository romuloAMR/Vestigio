package com.example.vestigioapi.dto.game.move;

import java.time.LocalDateTime;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.model.game.move.AnswerType;

public record MoveResponseDTO(
    Long id,
    String question,
    AnswerType answer,
    PlayerDTO author,
    LocalDateTime createdAt
) {}
