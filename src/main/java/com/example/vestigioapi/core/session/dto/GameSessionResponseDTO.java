package com.example.vestigioapi.core.session.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.core.session.model.GameStatus;

public record GameSessionResponseDTO<T, M>(
    Long id,
    String roomCode,
    GameStatus status,
    T content,
    PlayerDTO master,
    Set<PlayerDTO> players,
    List<M> moves,
    List<T> contentOptions,
    PlayerDTO winner,
    LocalDateTime createdAt
) {}
