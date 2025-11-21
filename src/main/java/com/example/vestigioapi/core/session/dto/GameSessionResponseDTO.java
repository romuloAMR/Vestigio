package com.example.vestigioapi.core.session.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.core.session.model.GameStatus;
import com.example.vestigioapi.vestigio.dto.MoveDTO;
import com.example.vestigioapi.vestigio.dto.StoryResponseDTO;

public record GameSessionResponseDTO(
    Long id,
    String roomCode,
    GameStatus status,
    StoryResponseDTO story,
    PlayerDTO master,
    Set<PlayerDTO> players,
    List<MoveDTO> moves,
    List<StoryResponseDTO> storyOptions,
    PlayerDTO winner,
    LocalDateTime createdAt
) {}
