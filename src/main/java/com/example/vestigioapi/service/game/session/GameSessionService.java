package com.example.vestigioapi.service.game.session;

import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.session.GameEvent;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final StoryRepository storyRepository;
    private final SimpMessagingTemplate messagingTemplate;

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

    @Transactional
    public GameSessionResponseDTO startGameSession(String roomCode, Long userId) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));

        if (!session.getMaster().getId().equals(userId)) {
            throw new AccessDeniedException("Only the master can start the game session.");
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Game session is not waiting for players. Current status: " + session.getStatus());
        }

        session.setStatus(GameStatus.IN_PROGRESS);

        GameSession startedSession = gameSessionRepository.save(session);

        GameEvent<GameSession> event = new GameEvent<>("GAME_STARTED", startedSession);
        messagingTemplate.convertAndSend("/topic/session/" + roomCode, event);

        return toResponseDTO(startedSession);
    }

    @Transactional
    public GameSessionResponseDTO finishGameSession(String roomCode, Long usuarioRequisitanteId) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));

        if (!session.getMaster().getId().equals(usuarioRequisitanteId)) {
            throw new AccessDeniedException("Only the master can finish the game session.");
        }

        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game session is not in progress.");
        }

        session.setStatus(GameStatus.COMPLETED);

        GameSession finishedSession = gameSessionRepository.save(session);

        GameEvent<GameSessionResponseDTO> event = new GameEvent<>("GAME_FINISHED", toResponseDTO(finishedSession));
        messagingTemplate.convertAndSend("/topic/session/" + roomCode, event);

        return toResponseDTO(finishedSession);
    }

    private String generateUniqueRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private GameSessionResponseDTO toResponseDTO(GameSession session) {
        StoryResponseDTO storyDTO = new StoryResponseDTO(
                session.getStory().getId(),
                session.getStory().getTitle(),
                session.getStory().getEnigmaticSituation(),
                session.getStory().getFullSolution(),
                session.getStory().getGenre(),
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
