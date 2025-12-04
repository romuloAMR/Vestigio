package com.example.vestigioapi.framework.engine;

import com.example.vestigioapi.framework.user.model.User;

import java.util.List;
import java.util.Map;

public interface GameEngine<S extends GameSession, C extends GameContent, M extends GameMoveDTO> {

    S createSession();
    
    boolean supports(GameSession session);

    void onGameStart(S session, Map<String, Object> configParams);

    Move processMove(S session, User player, String actionType, Map<String, Object> payload);

    boolean checkWinCondition(S session);

    void onGameEnd(S session);

    C getGameContent(S session, Long viewerId);

    List<C> getContentOptions(S session);

    List<M> getGameMoves(S session);
}
