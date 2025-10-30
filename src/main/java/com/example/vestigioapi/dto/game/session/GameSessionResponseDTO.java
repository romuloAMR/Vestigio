package com.example.vestigioapi.dto.game.session;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.session.GameStatus;

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
