package com.example.vestigioapi.framework.engine;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.session.repository.GameSessionRepository;
import com.example.vestigioapi.framework.session.repository.MoveRepository;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameOrchestratorService {

    private final GameSessionRepository sessionRepository;
    private final MoveRepository moveRepository;
    
    @SuppressWarnings("rawtypes")
    private final List<GameEngine> availableEngines; 
    
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @SuppressWarnings("unchecked")
    public Move processPlayerMove(String roomCode, User player, Object movePayload) {

        log.info("Processing move in room {} by user {}", roomCode, player.getUsername());
        GameSession session = findSession(roomCode);
        @SuppressWarnings("rawtypes")
        GameEngine engine = resolveEngine(session);

        Move move = engine.processMove(session, player, movePayload);

        move.setGameSession(session);
        move.setAuthor(player);
        Move savedMove = moveRepository.save(move);

        if (engine.checkWinCondition(session)) {
            engine.onGameEnd(session);
            sessionRepository.save(session);
        }

        notifyGameState(roomCode, savedMove);

        return savedMove;
    }

    @Transactional
    @SuppressWarnings("unchecked")
    public void startGame(String roomCode) {

        log.info("Starting game in room {}", roomCode);
        GameSession session = findSession(roomCode);
        @SuppressWarnings("rawtypes")
        GameEngine engine = resolveEngine(session);

        engine.onGameStart(session);
        
        sessionRepository.save(session);
        notifyGameState(roomCode, session);
    }

    private GameSession findSession(String roomCode) {
        return sessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND));
    }

    @SuppressWarnings("rawtypes")
    private GameEngine resolveEngine(GameSession session) {
        return availableEngines.stream()
            .filter(e -> e.supports(session)) 
            .findFirst()
            .orElseThrow(() -> {
                log.error("No engine found for session type: {}", session.getClass().getName());
                return new BusinessRuleException(ErrorMessages.ENGINE_NOT_FOUND);
            });
    }

    private void notifyGameState(String roomCode, Object payload) {
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, payload);
    }
}