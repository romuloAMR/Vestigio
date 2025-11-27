package com.example.vestigioapi.framework.session.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vestigioapi.framework.engine.GameOrchestratorService;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.session.dto.GameActionRequestDTO;
import com.example.vestigioapi.framework.session.dto.GameSessionResponseDTO;
import com.example.vestigioapi.framework.session.dto.GameStartRequestDTO;
import com.example.vestigioapi.framework.session.service.GameSessionService;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@SuppressWarnings({"rawtypes"})
@RestController
@RequestMapping("/api/v1/player/game-sessions")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final GameOrchestratorService gameOrchestratorService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<GameSessionResponseDTO> createGame(
            @Valid @RequestBody GameStartRequestDTO startDTO,
            @AuthenticationPrincipal User master) {

        GameSessionResponseDTO response = gameSessionService.createGameSession(
            startDTO.gameType(), 
            startDTO.configParams(), 
            master
        );

        messagingTemplate.convertAndSend("/topic/game/" + response.roomCode(), response);

        return ResponseEntity
            .created(URI.create("/api/v1/player/game-sessions/" + response.roomCode())) 
            .body(response);
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<GameSessionResponseDTO> joinGame(
            @PathVariable String roomCode,
            @AuthenticationPrincipal User player) {
        
        GameSessionResponseDTO response = gameSessionService.joinGameSession(roomCode, player);

        messagingTemplate.convertAndSend("/topic/game/" + response.roomCode(), response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<GameSessionResponseDTO> getGame(@PathVariable String roomCode) {
        GameSessionResponseDTO response = gameSessionService.getGameSessionByRoomCode(roomCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<Void> startGameSession(
            @PathVariable String roomCode, 
            @AuthenticationPrincipal User master) {
        gameOrchestratorService.startGame(roomCode);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomCode}/action")
    public ResponseEntity<Move> processAction(
            @PathVariable String roomCode,
            @RequestBody GameActionRequestDTO actionRequest, 
            @AuthenticationPrincipal User player) {

        Move move = gameOrchestratorService.processPlayerMove(roomCode, player, actionRequest);

        messagingTemplate.convertAndSend("/topic/game-sessions/" + roomCode + "/moves", move); 
        return ResponseEntity.ok(move);
    }

    @PostMapping("/{roomCode}/finish")
    public ResponseEntity<GameSessionResponseDTO> finishGameSession(
            @PathVariable String roomCode, 
            @AuthenticationPrincipal User master) {
        
        GameSessionResponseDTO finishedGame = gameSessionService.finishGameSession(roomCode, master.getId());
        return ResponseEntity.ok(finishedGame);
    }
}
