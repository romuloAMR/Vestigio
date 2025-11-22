package com.example.vestigioapi.framework.engine;

import com.example.vestigioapi.framework.user.model.User;

public interface GameEngine<T extends GameSession, M> {
    
    boolean supports(GameSession session);

    void onGameStart(T session);
    
    Move processMove(T session, User player, M movePayload);
    
    boolean checkWinCondition(T session);
    
    void onGameEnd(T session);
}
