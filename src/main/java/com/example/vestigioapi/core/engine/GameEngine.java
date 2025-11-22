package com.example.vestigioapi.core.engine;

import com.example.vestigioapi.core.user.model.User;

public interface GameEngine<T extends GameSession, M> {
    
    boolean supports(GameSession session);

    void onGameStart(T session);
    
    Move processMove(T session, User player, M movePayload);
    
    boolean checkWinCondition(T session);
    
    void onGameEnd(T session);
}
