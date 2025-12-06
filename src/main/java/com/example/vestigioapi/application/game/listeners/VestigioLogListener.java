package com.example.vestigioapi.application.game.listeners;

import com.example.vestigioapi.application.game.VestigioGameSession;
import com.example.vestigioapi.framework.engine.GameEventListener;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.user.model.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VestigioLogListener implements GameEventListener {

    @Override
    public boolean supports(GameSession session) {
        return session instanceof VestigioGameSession;
    }

    @Override
    public void onGameStart(GameSession session) {
        log.info("VESTIGIO: O jogo começou na sala " + session.getRoomCode());
    }

    @Override
    public void onMoveMade(GameSession session, Move move) {
        log.info("VESTIGIO: Jogada realizada por " + move.getAuthor().getName());
    }

    @Override
    public void onGameEnd(GameSession session, User user) {
        log.info("VESTIGIO: Vencedor é " + user.getName());
    }
}
