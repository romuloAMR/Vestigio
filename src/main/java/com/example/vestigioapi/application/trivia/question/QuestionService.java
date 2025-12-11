package com.example.vestigioapi.application.trivia.question;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;
import com.example.vestigioapi.application.trivia.question.dto.QuestionCreateDTO;
import com.example.vestigioapi.application.trivia.question.dto.QuestionResponseDTO;
import com.example.vestigioapi.framework.ai.game.TriviaAIService;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TriviaAIService triviaAIService;

    public QuestionResponseDTO createQuestion(QuestionCreateDTO dto, User creator) {
        Question question = Question.builder()
            .text(dto.text())
            .options(dto.options())
            .correctAnswerIndex(dto.correctAnswerIndex())
            .category(dto.category())
            .difficulty(dto.difficulty())
            .explanation(dto.explanation())
            .creator(creator)
            .build();

        Question saved = questionRepository.save(question);
        return toResponseDTO(saved);
    }

    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada com id: " + id));
        return toResponseDTO(question);
    }

    public List<QuestionResponseDTO> getQuestionsByCreator(User creator) {
        return questionRepository.findByCreator(creator)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public List<QuestionResponseDTO> getQuestionsByCategory(TriviaCategory category) {
        return questionRepository.findByCategory(category)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public List<QuestionResponseDTO> getRandomQuestions(int limit) {
        return questionRepository.findRandomQuestions(limit)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public List<QuestionResponseDTO> getRandomQuestionsByCategory(TriviaCategory category, int limit) {
        return questionRepository.findRandomQuestionsByCategory(category.name(), limit)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public QuestionResponseDTO updateQuestion(Long id, QuestionCreateDTO dto, User creator) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada com id: " + id));

        validateQuestionOwnership(question, creator);

        question.setText(dto.text());
        question.setOptions(dto.options());
        question.setCorrectAnswerIndex(dto.correctAnswerIndex());
        question.setCategory(dto.category());
        question.setDifficulty(dto.difficulty());
        question.setExplanation(dto.explanation());

        Question updated = questionRepository.save(question);
        return toResponseDTO(updated);
    }

    public void deleteQuestion(Long id, User creator) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pergunta não encontrada com id: " + id));

        validateQuestionOwnership(question, creator);

        questionRepository.delete(question);
    }

    private void validateQuestionOwnership(Question question, User creator) {
        if (question.getCreator() == null || !question.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenActionException("Você não tem permissão para essa ação com a pergunta");
        }
    }

    private QuestionResponseDTO toResponseDTO(Question question) {
        return new QuestionResponseDTO(
            question.getId(),
            question.getText(),
            question.getOptions(),
            question.getCategory(),
            question.getDifficulty(),
            question.getExplanation(),
            question.getCreator() != null ? question.getCreator().getName() : "System",
            question.getCreatedAt()
        );
    }

    public String generateAIQuestion(String category, String difficulty) {
        return triviaAIService.generateQuestion(category, difficulty);
    }

    public Integer getAIAnswer(String questionText, List<String> options) {
        return triviaAIService.getAIAnswer(questionText, options);
    }
}
