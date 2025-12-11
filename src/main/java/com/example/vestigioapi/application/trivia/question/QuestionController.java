package com.example.vestigioapi.application.trivia.question;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;
import com.example.vestigioapi.application.trivia.question.dto.QuestionCreateDTO;
import com.example.vestigioapi.application.trivia.question.dto.QuestionResponseDTO;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/trivia/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponseDTO> createQuestion(
            @Valid @RequestBody QuestionCreateDTO dto,
            @AuthenticationPrincipal User creator) {
        QuestionResponseDTO response = questionService.createQuestion(dto, creator);
        return ResponseEntity.created(URI.create("/api/v1/trivia/questions/" + response.id()))
            .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> getQuestion(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByCategory(
            @PathVariable TriviaCategory category) {
        return ResponseEntity.ok(questionService.getQuestionsByCategory(category));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getRandomQuestions(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(questionService.getRandomQuestions(limit));
    }

    @GetMapping("/random/category/{category}")
    public ResponseEntity<List<QuestionResponseDTO>> getRandomQuestionsByCategory(
            @PathVariable TriviaCategory category,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(questionService.getRandomQuestionsByCategory(category, limit));
    }

    @GetMapping("/my-questions")
    public ResponseEntity<List<QuestionResponseDTO>> getMyQuestions(
            @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(questionService.getQuestionsByCreator(creator));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionResponseDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionCreateDTO dto,
            @AuthenticationPrincipal User creator) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto, creator));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            @AuthenticationPrincipal User creator) {
        questionService.deleteQuestion(id, creator);
        return ResponseEntity.noContent().build();
    }
}
