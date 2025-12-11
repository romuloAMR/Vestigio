package com.example.vestigioapi.application.trivia.game;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.trivia.game.dto.QuestionResponseDTO;
import com.example.vestigioapi.application.trivia.game.dto.TriviaAnswerResponseDTO;
import com.example.vestigioapi.application.trivia.game.move.TriviaMove;
import com.example.vestigioapi.application.trivia.question.Question;
import com.example.vestigioapi.application.trivia.question.QuestionRepository;
import com.example.vestigioapi.application.trivia.ai.TriviaAIService;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.engine.GameEngine;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service("TRIVIA")
@RequiredArgsConstructor
public class TriviaGameEngine implements GameEngine<TriviaGameSession, QuestionResponseDTO, TriviaAnswerResponseDTO> {

    @Override
    public String getGameType() {
        return "TRIVIA";
    }
    private final QuestionRepository questionRepository;
    private final TriviaAIService triviaAIService;

    private static final int POINTS_EASY = 1;
    private static final int POINTS_MEDIUM = 2;
    private static final int POINTS_HARD = 3;

    @Override
    public boolean supports(GameSession session) {
        return session instanceof TriviaGameSession;
    }

    @Override
    public TriviaGameSession createSession() {
        return new TriviaGameSession();
    }

    @Override
    public void onGameStart(TriviaGameSession session, Map<String, Object> configParams) {
        int questionCount = configParams.containsKey("questionCount") 
            ? Integer.parseInt(configParams.get("questionCount").toString())
            : 10;

        String category = configParams.getOrDefault("category", "GERAL").toString();
        String difficulty = configParams.getOrDefault("difficulty", "EASY").toString();

        List<Question> questions = questionRepository.findRandomQuestions(questionCount);

        if (questions.size() < questionCount) {
            int toGenerate = questionCount - questions.size();
            for (int i = 0; i < toGenerate; i++) {
                Question aiQ = triviaAIService.generateQuestionEntity(category, difficulty);
                questions.add(questionRepository.save(aiQ));
            }
        }

        session.setTotalQuestions(questions.size());
        session.setCurrentQuestionIndex(0);
        session.setPlayerScore(0);

        if (!questions.isEmpty()) {
            session.setCurrentQuestionId(questions.get(0).getId());
        }
    }

    @Override
    public Move processMove(TriviaGameSession session, User player, String actionType, Map<String, Object> payload) {
        if (!"ANSWER_QUESTION".equals(actionType)) {
            throw new BusinessRuleException("Tipo de ação inválido: " + actionType);
        }

        Integer selectedAnswerIndex = (Integer) payload.get("selectedAnswerIndex");
        
        Long currentQuestionId = session.getCurrentQuestionId();
        Question question = questionRepository.findById(currentQuestionId)
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada"));

        boolean isCorrect = selectedAnswerIndex != null && 
                           selectedAnswerIndex.equals(question.getCorrectAnswerIndex());
        
        int points = isCorrect ? getPointsForDifficulty(question.getDifficulty()) : 0;
        
        if (isCorrect) {
            session.setPlayerScore(session.getPlayerScore() + points);
        }

        TriviaMove move = new TriviaMove();
        move.setAuthor(player);
        move.setGameSession(session);
        move.setQuestionId(question.getId());
        move.setSelectedAnswerIndex(selectedAnswerIndex);
        move.setIsCorrect(isCorrect);
        move.setPointsEarned(points);

        return move;
    }

    @Override
    public boolean checkWinCondition(TriviaGameSession session) {
        return session.getCurrentQuestionIndex() >= session.getTotalQuestions();
    }

    @Override
    public void onGameEnd(TriviaGameSession session) {
        session.setCurrentQuestionIndex(session.getTotalQuestions());
        session.setCurrentQuestionId(null);
    }

    @Override
    public QuestionResponseDTO getGameContent(TriviaGameSession session, Long viewerId) {
        Question question = questionRepository.findById(session.getCurrentQuestionId())
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada"));

        return new QuestionResponseDTO(
            question.getId(),
            question.getText(),
            question.getOptions(),
            question.getCategory(),
            question.getDifficulty(),
            session.getPlayerScore(),
            session.getTotalQuestions(),
            session.getCurrentQuestionIndex() + 1
        );
    }

    @Override
    public List<QuestionResponseDTO> getContentOptions(TriviaGameSession session) {
        return Collections.emptyList();
    }

    @Override
    public List<TriviaAnswerResponseDTO> getGameMoves(TriviaGameSession session) {
        if (session.getTriviaMove() == null) return Collections.emptyList();

        return session.getTriviaMove().stream()
            .map(move -> new TriviaAnswerResponseDTO(
                move.getQuestionId(),
                move.getSelectedAnswerIndex(),
                move.getIsCorrect(),
                questionRepository.findById(move.getQuestionId())
                    .map(q -> q.getCorrectAnswerIndex())
                    .orElse(-1),
                move.getPointsEarned(),
                session.getPlayerScore(),
                questionRepository.findById(move.getQuestionId())
                    .map(q -> q.getExplanation())
                    .orElse("")
            ))
            .collect(Collectors.toList());
    }

    private int getPointsForDifficulty(com.example.vestigioapi.application.trivia.question.constants.Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> POINTS_EASY;
            case MEDIUM -> POINTS_MEDIUM;
            case HARD -> POINTS_HARD;
        };
    }

    public Integer getAIAnswer(Question question) {
        return triviaAIService.getAIAnswer(question.getText(), question.getOptions());
    }
}
