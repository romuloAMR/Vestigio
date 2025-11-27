package com.example.vestigioapi.framework.session.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import com.example.vestigioapi.framework.engine.GameOrchestratorService;
import com.example.vestigioapi.framework.session.dto.GameActionRequestDTO;
import com.example.vestigioapi.framework.session.dto.GameSessionResponseDTO;
import com.example.vestigioapi.framework.session.service.GameSessionService;
import com.example.vestigioapi.framework.user.model.User;
import com.example.vestigioapi.framework.user.service.UserService;

import lombok.RequiredArgsConstructor;

@SuppressWarnings({"rawtypes"})
@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final GameSessionService gameSessionService;
    private final GameOrchestratorService gameOrchestratorService;
    private final UserService userService;

    @MessageMapping("/game/{roomCode}/action")
    public void handleAction(@DestinationVariable String roomCode, 
                             @Payload GameActionRequestDTO action, 
                             Principal principal) {
        
        User user = userService.getAuthenticatedUser(principal);
        gameOrchestratorService.processPlayerMove(roomCode, user, action);
    }

    @SubscribeMapping("/game/{roomCode}")
    public GameSessionResponseDTO onSubscribe(@DestinationVariable String roomCode, Principal principal) {
        return gameSessionService.getGameSessionByRoomCode(roomCode);
    }
}
