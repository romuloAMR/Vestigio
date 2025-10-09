package com.example.vestigioapi.controller.game.session;

import com.example.vestigioapi.dto.game.move.AnswerQuestionRequestDTO;
import com.example.vestigioapi.dto.game.move.AskQuestionRequestDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.service.game.session.GameSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
public class GameWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final GameSessionService gameSessionService;

    @MessageMapping("/game/{roomCode}/select-story")
    public void selectStory(@DestinationVariable String roomCode, 
                            @Payload Long storyId, 
                            Principal principal) {
        
        User user = getUserFromPrincipal(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.selectStoryAndStartGame(roomCode, storyId, user);
        
        broadcastGameState(roomCode, updatedGame);
    }

    @MessageMapping("/game/{roomCode}/ask")
    public void askQuestion(@DestinationVariable String roomCode, 
                            @Payload AskQuestionRequestDTO dto, 
                            Principal principal) {
        
        User user = getUserFromPrincipal(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.askQuestion(roomCode, dto, user);
        
        broadcastGameState(roomCode, updatedGame);
    }

    @SubscribeMapping("/game/{roomCode}")
    public GameSessionResponseDTO onSubscribe(@DestinationVariable String roomCode) {
        System.out.println("Novo cliente inscrito na sala: " + roomCode);
        return gameSessionService.getGameSessionByRoomCode(roomCode);
    }

    @MessageMapping("/game/{roomCode}/answer")
    public void answerQuestion(@DestinationVariable String roomCode, 
                               @Payload AnswerQuestionRequestDTO dto, 
                               Principal principal) {
        
        User user = getUserFromPrincipal(principal);
        GameSessionResponseDTO updatedGame = gameSessionService.answerQuestion(roomCode, dto, user);

        broadcastGameState(roomCode, updatedGame);
    }

    @MessageMapping("/game/{roomCode}/end")
        public void endGame(@DestinationVariable String roomCode, Principal principal) {
            User user = getUserFromPrincipal(principal);
            GameSessionResponseDTO updatedGame = gameSessionService.endGame(roomCode, user);
            
            broadcastGameState(roomCode, updatedGame);
        }

    private void broadcastGameState(String roomCode, GameSessionResponseDTO gameSession) {
        messagingTemplate.convertAndSend("/topic/game/" + roomCode, gameSession);
    }

    private User getUserFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            return (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        }
        throw new IllegalArgumentException("Tipo de Principal inválido ou usuário não autenticado.");
    }
}