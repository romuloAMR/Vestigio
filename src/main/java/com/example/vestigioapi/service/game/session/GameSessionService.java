package com.example.vestigioapi.service.game.session;

import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.game.session.GameStatus;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.GameSessionRepository;
import com.example.vestigioapi.repository.StoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final StoryRepository storyRepository;

    public GameSessionResponseDTO createGameSession(GameSessionCreateDTO dto, User master) {
        Story story = storyRepository.findById(dto.storyId())
                .orElseThrow(() -> new EntityNotFoundException("Story not found with id: " + dto.storyId()));

        GameSession session = new GameSession();
        session.setMaster(master);
        session.setStory(story);
        session.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        session.setRoomCode(generateUniqueRoomCode());
        session.getPlayers().add(master);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    public GameSessionResponseDTO joinGameSession(String roomCode, User player) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Cannot join a game that has already started or finished.");
        }

        session.getPlayers().add(player);
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    public GameSessionResponseDTO getGameSessionByRoomCode(String roomCode) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));
        return toResponseDTO(session);
    }

    private String generateUniqueRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private GameSessionResponseDTO toResponseDTO(GameSession session) {
        StoryResponseDTO storyDTO = new StoryResponseDTO(
                session.getStory().getId(),
                session.getStory().getTitle(),
                session.getStory().getEnigmaticSituation(),
                session.getStory().getDifficulty(),
                session.getStory().getCreator().getUsername()
        );

        PlayerDTO masterDTO = new PlayerDTO(session.getMaster().getId(), session.getMaster().getUsername());

        return new GameSessionResponseDTO(
                session.getId(),
                session.getRoomCode(),
                session.getStatus(),
                storyDTO,
                masterDTO,
                session.getPlayers().stream()
                        .map(player -> new PlayerDTO(player.getId(), player.getUsername()))
                        .collect(Collectors.toSet()),
                session.getCreatedAt()
        );
    }   
}
