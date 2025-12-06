package com.example.vestigioapi.application.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.vestigioapi.application.dto.AnswerQuestionRequestDTO;
import com.example.vestigioapi.application.dto.AskQuestionRequestDTO;
import com.example.vestigioapi.application.dto.MoveResponseDTO;
import com.example.vestigioapi.application.dto.StoryResponseDTO;
import com.example.vestigioapi.application.model.move.VestigioMove;
import com.example.vestigioapi.application.model.session.VestigioGameSession;
import com.example.vestigioapi.application.model.story.Story;
import com.example.vestigioapi.application.repository.StoryRepository;
import com.example.vestigioapi.application.util.VestigioErrorMessages;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.engine.GameEngine;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service("VESTIGIO") 
@RequiredArgsConstructor
public class VestigioGameEngine implements GameEngine<VestigioGameSession, StoryResponseDTO, MoveResponseDTO> {

    private final StoryRepository storyRepository;
    private final ObjectMapper objectMapper;

    public static final String ACTION_ASK = "ASK_QUESTION";
    public static final String ACTION_ANSWER = "ANSWER_QUESTION";

    @Override
    public boolean supports(GameSession session) {
        return session instanceof VestigioGameSession;
    }

    @Override
    public VestigioGameSession createSession() {
        return new VestigioGameSession();
    }

    @Override
    public void onGameStart(VestigioGameSession session, Map<String, Object> configParams) {
        if (!configParams.containsKey("storyId")) {
            throw new BusinessRuleException(VestigioErrorMessages.VESTIGIO_STORY_ID_REQUIRED);
        }

        Long storyId = Long.valueOf(configParams.get("storyId").toString());

        Story story = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException("História não encontrada com ID: " + storyId));

        session.setCurrentStory(story);
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
        AskQuestionRequestDTO dto;
        try {
            dto = objectMapper.convertValue(payload, AskQuestionRequestDTO.class);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Formato da jogada inválido.");
        }

        VestigioMove move = new VestigioMove();
        move.setQuestion(dto.questionText());
        move.setAuthor(actor);
        move.setCreatedAt(LocalDateTime.now());
        
        return move;
    }

    private Move handleAnswerQuestion(VestigioGameSession session, User actor, Map<String, Object> payload) {
        AnswerQuestionRequestDTO dto;
        try {
            dto = objectMapper.convertValue(payload, AnswerQuestionRequestDTO.class);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Formato da resposta inválido.");
        }

        VestigioMove targetMove = (VestigioMove) session.getMoves().stream()
            .filter(m -> m.getId().equals(dto.moveId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVE_NOT_FOUND));

        targetMove.setAnswer(dto.answer());
        
        return targetMove;
    }

    @Override
    public boolean checkWinCondition(VestigioGameSession session) {
        return session.getWinner() != null;
    }

    @Override
    public void onGameEnd(VestigioGameSession session) {
        //TODO: fazer
    }

    @Override
    public StoryResponseDTO getGameContent(VestigioGameSession session, Long viewerId) {
        if (session.getCurrentStory() == null) return null;
        
        boolean isMaster = session.getMaster().getId().equals(viewerId);
        Story story = session.getCurrentStory();

        return new StoryResponseDTO(
            story.getId(),
            story.getTitle(),
            story.getEnigmaticSituation(),
            isMaster ? story.getFullSolution() : null,
            story.getGenre(),
            story.getDifficulty(),
            story.getCreator() != null ? story.getCreator().getName() : "System"
        );
    }

    @Override
    public List<StoryResponseDTO> getContentOptions(VestigioGameSession session) {
        if (session.getStoryOptions() == null) return Collections.emptyList();
        
        return session.getStoryOptions().stream()
            .map(this::toStoryDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<MoveResponseDTO> getGameMoves(VestigioGameSession session) {
        if (session.getMoves() == null) return Collections.emptyList();

        return session.getMoves().stream()
            .map(m -> (VestigioMove) m)
            .sorted(Comparator.comparing(VestigioMove::getCreatedAt))
            .map(this::toMoveDTO)
            .collect(Collectors.toList());
    }

    private StoryResponseDTO toStoryDTO(Story story) {
        return new StoryResponseDTO(
            story.getId(),
            story.getTitle(),
            story.getEnigmaticSituation(),
            story.getFullSolution(),
            story.getGenre(),
            story.getDifficulty(),
            story.getCreator() != null ? story.getCreator().getName() : "System"
        );
    }

    private MoveResponseDTO toMoveDTO(VestigioMove move) {
        return new MoveResponseDTO(
            move.getId(),
            move.getQuestion(),
            move.getAnswer(),
            new PlayerDTO(move.getAuthor().getId(), move.getAuthor().getUsername()),
            move.getCreatedAt()
        );
    }
}
