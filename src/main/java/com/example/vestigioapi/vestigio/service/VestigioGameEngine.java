package com.example.vestigioapi.vestigio.service;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.core.engine.GameEngine;
import com.example.vestigioapi.core.engine.GameSession;
import com.example.vestigioapi.core.engine.Move;
import com.example.vestigioapi.core.user.model.User;
import com.example.vestigioapi.vestigio.model.VestigioGameSession;

@Service
public class VestigioGameEngine implements GameEngine<VestigioGameSession, String> {

    @Override
    public void onGameStart(VestigioGameSession session) {
        // TODO Lógica para sortear a história inicial, se necessário
    }

    @Override
    public Move processMove(VestigioGameSession session, User player, String question) {
        // TODO Lógica da jogada
        return null; // Retornar o move criado
    }

    @Override
    public boolean checkWinCondition(VestigioGameSession session) {
        // TODO Lógica para verificar se o mistério foi resolvido
        return false;
    }

    @Override
    public void onGameEnd(VestigioGameSession session) {
        // TODO Limpeza ou cálculo de pontuação final
    }

    @Override
    public boolean supports(GameSession session) {
        // TODO Verificação
        throw null;
    }
}
