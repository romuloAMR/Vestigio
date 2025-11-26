package com.example.vestigioapi.framework.session.controller;

import com.example.vestigioapi.framework.session.dto.GameSessionResponseDTO;
import com.example.vestigioapi.framework.session.dto.PickWinnerRequestDTO;
import com.example.vestigioapi.framework.session.service.GameSessionService;
import com.example.vestigioapi.framework.user.model.User;
import com.example.vestigioapi.framework.user.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionService gameSessionService;
    private final UserService userService;

    @MessageMapping("/game/{roomCode}/select-story")
    public void selectStory(@DestinationVariable String roomCode, 
                            @Payload Long storyId, 
                            Principal principal) {
        
        User user = userService.getAuthenticatedUser(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.selectStoryAndStartGame(roomCode, storyId, user);
        
        broadcastGameState(roomCode, updatedGame);
    }

    @MessageMapping("/game/{roomCode}/ask")
    public void askQuestion(@DestinationVariable String roomCode, 
                            @Payload AskQuestionRequestDTO dto, 
                            Principal principal) {
        
        User user = userService.getAuthenticatedUser(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.askQuestion(roomCode, dto, user);
        
        broadcastGameState(roomCode, updatedGame);
    }

    @SubscribeMapping("/game/{roomCode}")
    public GameSessionResponseDTO onSubscribe(@DestinationVariable String roomCode, Principal principal) {
        return gameSessionService.getGameSessionByRoomCode(roomCode);
    }

    @MessageMapping("/game/{roomCode}/answer")
    public void answerQuestion(@DestinationVariable String roomCode, 
                               @Payload AnswerQuestionRequestDTO dto, 
                               Principal principal) {
        
        User user = userService.getAuthenticatedUser(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.answerQuestion(roomCode, dto, user);

        broadcastGameState(roomCode, updatedGame);
    }

    @MessageMapping("/game/{roomCode}/pick-winner")
    public void pickWinner(@DestinationVariable String roomCode,
                           @Payload PickWinnerRequestDTO dto,
                           Principal principal) {
        User user = userService.getAuthenticatedUser(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.pickWinner(roomCode, dto.winnerId(), user);
        broadcastGameState(roomCode, updatedGame);
    }
    
    @MessageMapping("/game/{roomCode}/end")
        public void endGame(@DestinationVariable String roomCode, Principal principal) {
            User user = userService.getAuthenticatedUser(principal);
            GameSessionResponseDTO updatedGame = gameSessionService.endGame(roomCode, user);
            
            broadcastGameState(roomCode, updatedGame);
        }

    private void broadcastGameState(String roomCode, GameSessionResponseDTO gameSession) {
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, gameSession);
    }
}