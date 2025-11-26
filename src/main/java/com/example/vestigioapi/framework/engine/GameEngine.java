package com.example.vestigioapi.framework.engine;

import com.example.vestigioapi.framework.user.model.User;
import java.util.Map;

public interface GameEngine<T extends GameSession> {
    
    boolean supports(GameSession session);

    void onGameStart(T session, Map<String, Object> configParams);
    
    Move processMove(T session, User player, String actionType, Map<String, Object> payload);
    
    boolean checkWinCondition(T session);
    
    void onGameEnd(T session);

    void onGameStart(VestigioGameSession session);

    Move processMove(VestigioGameSession session, User player, String question);
}
