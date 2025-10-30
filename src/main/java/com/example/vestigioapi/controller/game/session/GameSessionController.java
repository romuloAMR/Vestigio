package com.example.vestigioapi.controller.game.session;

import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vestigioapi.dto.game.move.AnswerRequestDTO;
import com.example.vestigioapi.dto.game.move.MoveResponseDTO;
import com.example.vestigioapi.dto.game.move.QuestionRequestDTO;
import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.service.game.move.MoveService;
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
    private final MoveService jogadaService;
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

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<GameSessionResponseDTO> startGameSession(@PathVariable String roomCode, @AuthenticationPrincipal User master) {
        GameSessionResponseDTO startedGame = gameSessionService.startGameSession(roomCode, master.getId());
        return ResponseEntity.ok(startedGame);
    }
    
    @PostMapping("/{roomCode}/finish")
    public ResponseEntity<GameSessionResponseDTO> finishGameSession(@PathVariable String roomCode, @AuthenticationPrincipal User master) {
        GameSessionResponseDTO finishedGame = gameSessionService.finishGameSession(roomCode, master.getId());
        return ResponseEntity.ok(finishedGame);
    }

    @PostMapping("/{roomCode}/ask-question")
    public ResponseEntity<MoveResponseDTO> askQuestion(
            @PathVariable String roomCode,
            @RequestBody QuestionRequestDTO request, 
            @AuthenticationPrincipal User player) {
        MoveResponseDTO move = jogadaService.processQuestion(roomCode, player.getId(), request.question());

        messagingTemplate.convertAndSend("/topic/game-sessions/" + roomCode + "/moves", move);
        return ResponseEntity.ok(move);
    }
    
    @PutMapping("{roomCode}/moves/{moveId}/answer-question")
    public ResponseEntity<MoveResponseDTO> answerQuestion(
            @PathVariable String roomCode,
            @PathVariable Long moveId,
            @RequestBody AnswerRequestDTO request,
            @AuthenticationPrincipal User master) {
        MoveResponseDTO move = jogadaService.processAnswer(moveId, master.getId(), request.answer());
        
        messagingTemplate.convertAndSend("/topic/game-sessions/" + roomCode + "/moves", move);
        return ResponseEntity.ok(move);
    }

}
