package com.example.vestigioapi.framework.session.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.framework.engine.GameContent;
import com.example.vestigioapi.framework.engine.GameMoveDTO;
import com.example.vestigioapi.framework.session.model.GameStatus;

public record GameSessionResponseDTO<T extends GameContent, M extends GameMoveDTO>(
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
