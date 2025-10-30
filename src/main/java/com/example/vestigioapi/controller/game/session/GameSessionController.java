package com.example.vestigioapi.controller.game.session;

import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;
import com.example.vestigioapi.service.game.session.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/player/game-sessions")
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<GameSessionResponseDTO> createGame(
            @Valid @RequestBody GameSessionCreateDTO createDTO,
            @AuthenticationPrincipal User master) {

        GameSessionResponseDTO response = gameSessionService.createGameSession(createDTO, master);

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
