package com.example.vestigioapi.framework.engine;

import java.util.Collections;
import java.util.List;

import jakarta.persistence.EntityManager;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.session.dto.GameActionRequestDTO;
import com.example.vestigioapi.framework.session.dto.GameSessionResponseDTO;
import com.example.vestigioapi.framework.session.repository.GameSessionRepository;
import com.example.vestigioapi.framework.session.repository.MoveRepository;
import com.example.vestigioapi.framework.session.service.GameSessionService;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"rawtypes", "unchecked"})
@Service
@RequiredArgsConstructor
@Slf4j
public class GameOrchestratorService {

    private final GameSessionRepository sessionRepository;
    private final MoveRepository moveRepository;
    private final GameSessionService gameSessionService;
    private final List<GameEngine> availableEngines;     
    private final SimpMessagingTemplate messagingTemplate;
    private final List<GameRule> gameRules;
    private final List<GameEventListener> gameEventListeners;
    private final EntityManager entityManager;

    @Transactional
    public Move processPlayerMove(String roomCode, User player, GameActionRequestDTO actionRequest) {
        GameSession session = findSession(roomCode);

        gameRules.stream()
            .filter(rule -> rule.supports(session))
            .forEach(rule -> rule.validate(
                session, 
                player, 
                actionRequest.actionType(), 
                actionRequest.payload()
            ));

        GameEngine engine = resolveEngine(session);
        
        Move move = engine.processMove(
            session, 
            player, 
            actionRequest.actionType(), 
            actionRequest.payload()
        );
        move.setGameSession(session);
        move.setAuthor(player);

        Move savedMove = moveRepository.save(move);
        sessionRepository.save(session);
        entityManager.flush();
        entityManager.clear();
        final GameSession refreshedSession = sessionRepository.findById(session.getId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND));

        gameEventListeners.stream()
            .filter(l -> l.supports(refreshedSession))
            .forEach(l -> l.onMoveMade(refreshedSession, savedMove));

        if (engine.checkWinCondition(refreshedSession)) {
            engine.onGameEnd(refreshedSession);
            sessionRepository.save(refreshedSession);

            gameEventListeners.stream()
            .filter(l -> l.supports(refreshedSession))
            .forEach(l -> l.onGameEnd(refreshedSession, refreshedSession.getWinner()));
        }

        GameSessionResponseDTO<?, ?> responseDTO = gameSessionService
            .toResponseDTO(refreshedSession, refreshedSession.getMaster().getId());
        
        System.out.println("[GameOrchestrator] Broadcasting update. Moves count: " + 
            (responseDTO.moves() != null ? responseDTO.moves().size() : "null"));

        notifyGameState(roomCode, responseDTO);

        return savedMove;
    }

    @Transactional
    public void startGame(String roomCode) {
        GameSession session = findSession(roomCode);
        GameEngine engine = resolveEngine(session);

        engine.onGameStart(session, Collections.emptyMap());
        
        session.setStatus(com.example.vestigioapi.framework.session.model.GameStatus.IN_PROGRESS);
        
        GameSession savedSession = sessionRepository.save(session);

        gameEventListeners.stream()
            .filter(l -> l.supports(savedSession))
            .forEach(l -> l.onGameStart(savedSession));

        GameSessionResponseDTO<?, ?> responseDTO = gameSessionService
            .toResponseDTO(savedSession, savedSession.getMaster().getId());
        notifyGameState(roomCode, responseDTO);
    }

    private GameSession findSession(String roomCode) {
        return sessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND));
    }

    private GameEngine resolveEngine(GameSession session) {
        return availableEngines.stream()
            .filter(e -> e.supports(session)) 
            .findFirst()
            .orElseThrow(() -> {
                return new BusinessRuleException(ErrorMessages.ENGINE_NOT_FOUND);
            });
    }

    private void notifyGameState(String roomCode, Object payload) {
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, payload);
    }
}
