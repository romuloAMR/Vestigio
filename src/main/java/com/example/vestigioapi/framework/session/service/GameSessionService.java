package com.example.vestigioapi.framework.session.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.engine.GameContent;
import com.example.vestigioapi.framework.engine.GameEngine;
import com.example.vestigioapi.framework.engine.GameMoveDTO;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.session.dto.GameSessionResponseDTO;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;
import com.example.vestigioapi.framework.session.model.GameStatus;
import com.example.vestigioapi.framework.session.repository.GameSessionRepository;
import com.example.vestigioapi.framework.user.model.User;
import com.example.vestigioapi.framework.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;
    private final Map<String, GameEngine<?, ?, ?>> engineRegistry;

    @Transactional
    public GameSessionResponseDTO<?, ?> createGameSession(String gameType, Map<String, Object> params, User master) {
        GameEngine engine = engineRegistry.get(gameType);
        
        if (engine == null){
            throw new BusinessRuleException("Tipo de jogo inválido ou Engine não registrada: " + gameType);
        }

        GameSession session = (GameSession) engine.createSession();

        session.setMaster(master);
        session.setRoomCode(generateUniqueRoomCode());
        session.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        session.getPlayers().add(master);

        callEngineOnGameStart(engine, session, params);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Transactional
    public GameSessionResponseDTO<?, ?> joinGameSession(String roomCode, User player) {
        GameSession session = findSessionByRoomCodeOrThrow(roomCode);

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_INVALID_JOIN);
        }

        User managedPlayer = userRepository.findById(player.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (session.getPlayers().stream().noneMatch(p -> p.getId().equals(managedPlayer.getId()))) {
            session.getPlayers().add(managedPlayer);
        }
        
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    @Transactional(readOnly = true)
    public GameSessionResponseDTO<?, ?> getGameSessionByRoomCode(String roomCode) {
        GameSession session = findSessionByRoomCodeOrThrow(roomCode);
        return toResponseDTO(session);
    }

    @Transactional
    public GameSessionResponseDTO<?, ?> finishGameSession(String roomCode, Long requesterId) {
        GameSession session = findSessionByRoomCodeOrThrow(roomCode);

        if (!session.getMaster().getId().equals(requesterId)) {
            throw new BusinessRuleException(ErrorMessages.FORBIDDEN_MASTER_ONLY_END);
        }

        session.setStatus(GameStatus.COMPLETED);
        
        resolveEngine(session).onGameEnd(session);

        GameSession saved = gameSessionRepository.save(session);
        return toResponseDTO(saved);
    }

    private GameSession findSessionByRoomCodeOrThrow(String roomCode) {
        return gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + ": " + roomCode));
    }

    private String generateUniqueRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private GameSessionResponseDTO<?, ?> toResponseDTO(GameSession session) {
        GameEngine engine = resolveEngine(session);
        GameContent content = (GameContent) engine.getGameContent(session); 
        List<GameContent> options = engine.getContentOptions(session);
        List<? extends GameMoveDTO> moves = engine.getGameMoves(session);

        PlayerDTO masterDTO = new PlayerDTO(
            session.getMaster().getId(), 
            session.getMaster().getUsername()
        );

        Set<PlayerDTO> playersDTO = session.getPlayers().stream()
                .map(player -> new PlayerDTO(player.getId(), player.getUsername()))
                .collect(Collectors.toSet());
        
        PlayerDTO winnerDTO = session.getWinner() != null
            ? new PlayerDTO(session.getWinner().getId(), session.getWinner().getUsername())
            : null;

        return new GameSessionResponseDTO(
                session.getId(),
                session.getRoomCode(),
                session.getStatus(),
                content,       
                masterDTO,
                playersDTO,
                moves,
                options,
                winnerDTO,
                session.getCreatedAt()
        );
    }

    private GameEngine resolveEngine(GameSession session) {
        return engineRegistry.values().stream()
            .filter(e -> e.supports(session))
            .findFirst()
            .orElseThrow(() -> new BusinessRuleException(ErrorMessages.ENGINE_NOT_FOUND));
    }

    private void callEngineOnGameStart(GameEngine engine, GameSession session, Map<String, Object> params) {
        engine.onGameStart(session, params);
    }
}
