package com.example.vestigioapi.application.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.dto.MoveResponseDTO;
import com.example.vestigioapi.application.dto.StoryResponseDTO;
import com.example.vestigioapi.application.model.move.AnswerType;
import com.example.vestigioapi.application.model.session.VestigioGameSession;
import com.example.vestigioapi.application.model.story.Story;
import com.example.vestigioapi.application.model.move.VestigioMove;
import com.example.vestigioapi.application.repository.StoryRepository;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.engine.GameEngine;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VestigioGameEngine implements GameEngine<VestigioGameSession, StoryResponseDTO, MoveResponseDTO> {

    private final StoryRepository storyRepository;

    public static final String ACTION_ASK = "ASK_QUESTION";
    public static final String ACTION_ANSWER = "ANSWER_QUESTION";

    @Override
    public boolean supports(GameSession session) {
        return session instanceof VestigioGameSession;
    }

    @Override
    public void onGameStart(VestigioGameSession session, Map<String, Object> configParams) {
        // O Framework passa um Map genérico. A Engine sabe que precisa de um "storyId".
        if (!configParams.containsKey("storyId")) {
            throw new BusinessRuleException("Para iniciar Vestígio, é obrigatório informar o 'storyId'.");
        }

        Long storyId = Long.valueOf(configParams.get("storyId").toString());

        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("História não encontrada com ID: " + storyId));

        session.setCurrentStory(story);
        // Aqui você poderia setar outras coisas específicas do Vestígio, como tempo limite, dicas iniciais, etc.
    }

    @Override
    public Move processMove(VestigioGameSession session, User actor, String actionType, Map<String, Object> payload) {
        switch (actionType) {
            case ACTION_ASK:
                return handleAskQuestion(session, actor, payload);
            case ACTION_ANSWER:
                return handleAnswerQuestion(session, actor, payload);
            default:
                throw new BusinessRuleException("Tipo de ação desconhecido para Vestígio: " + actionType);
        }
    }

    private Move handleAskQuestion(VestigioGameSession session, User actor, Map<String, Object> payload) {
        // 1. Validações de Regra de Negócio Específicas do Jogo
        if (session.getMaster().getId().equals(actor.getId())) {
            throw new ForbiddenActionException("O Mestre não pode fazer perguntas.");
        }
        
        if (!payload.containsKey("question")) {
            throw new BusinessRuleException("Payload deve conter o campo 'question'.");
        }

        String questionText = (String) payload.get("question");

        // 2. Criação do Movimento Específico
        VestigioMove move = new VestigioMove();
        move.setQuestion(questionText);
        move.setAuthor(actor);
        move.setCreatedAt(LocalDateTime.now());
        // O framework vai associar a sessão e salvar no banco depois
        
        return move;
    }

    private Move handleAnswerQuestion(VestigioGameSession session, User actor, Map<String, Object> payload) {
        // 1. Validações
        if (!session.getMaster().getId().equals(actor.getId())) {
            throw new ForbiddenActionException("Apenas o Mestre pode responder.");
        }

        if (!payload.containsKey("moveId") || !payload.containsKey("answer")) {
            throw new BusinessRuleException("Payload deve conter 'moveId' e 'answer'.");
        }

        Long moveId = Long.valueOf(payload.get("moveId").toString());
        String answerString = (String) payload.get("answer");
        AnswerType answerType = AnswerType.valueOf(answerString); // Assume que o enum bate com a string

        // 2. Busca o movimento existente na lista da sessão
        VestigioMove targetMove = (VestigioMove) session.getMoves().stream()
            .filter(m -> m.getId().equals(moveId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada nesta sessão."));

        if (targetMove.getAnswer() != null) {
            throw new BusinessRuleException("Esta pergunta já foi respondida.");
        }

        // 3. Atualiza o movimento
        targetMove.setAnswer(answerType);
        
        // Retornamos o movimento modificado para o framework salvar/notificar
        return targetMove;
    }

    @Override
    public boolean checkWinCondition(VestigioGameSession session) {
        // TODO Lógica futura: O mestre pode ter um botão "Mistério Resolvido" que aciona um flag na sessão
        return false;
    }

    @Override
    public void onGameEnd(VestigioGameSession session) {
        // TODO Lógica de limpeza ou pontuação final
    }

    @Override
    public void onGameStart(VestigioGameSession session) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onGameStart'");
    }

    @Override
    public Move processMove(VestigioGameSession session, User player, String question) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processMove'");
    }
}