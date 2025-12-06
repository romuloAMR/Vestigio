package com.example.vestigioapi.framework.engine;

import com.example.vestigioapi.framework.user.model.User;

public interface GameEventListener {
    boolean supports(GameSession session);
    default void onGameStart(GameSession session) {}
    default void onMoveMade(GameSession session, Move move) {}
    default void onGameEnd(GameSession session, User winner) {}
}
