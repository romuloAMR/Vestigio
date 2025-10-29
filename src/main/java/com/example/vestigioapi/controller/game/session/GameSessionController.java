package com.example.vestigioapi.controller.game.session;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.service.game.session.GameSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/player/game-sessions")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @PostMapping
    public ResponseEntity<GameSessionResponseDTO> createGame(
            @Valid @RequestBody GameSessionCreateDTO createDTO,
            @AuthenticationPrincipal User master) {

        GameSessionResponseDTO response = gameSessionService.createGameSession(createDTO, master);

        return ResponseEntity
            .created(URI.create("/api/v1/player/game-sessions/" + response.roomCode())) 
            .body(response);
    }

    @PostMapping("/{roomCode}/join")
    public ResponseEntity<GameSessionResponseDTO> joinGame(
            @PathVariable String roomCode,
            @AuthenticationPrincipal User player) {
        
        GameSessionResponseDTO response = gameSessionService.joinGameSession(roomCode, player);
        return ResponseEntity
            .ok(response);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<GameSessionResponseDTO> getGame(@PathVariable String roomCode) {
        GameSessionResponseDTO response = gameSessionService.getGameSessionByRoomCode(roomCode);
        return ResponseEntity
            .ok(response);
    }

    @PostMapping("/{roomCode}/start-selection")
    public ResponseEntity<GameSessionResponseDTO> startStorySelection(
            @PathVariable String roomCode,
            @AuthenticationPrincipal User user) {
        
        GameSessionResponseDTO response = gameSessionService.startStorySelection(roomCode, user);
        return ResponseEntity.ok(response);
    }
}
