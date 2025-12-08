package com.example.vestigioapi.application.vestigio.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.vestigioapi.application.vestigio.ai.dto.EvaluateStoryRequest;
import com.example.vestigioapi.application.vestigio.ai.dto.GenerateSituationRequest;
import com.example.vestigioapi.application.vestigio.ai.dto.GenerateSolutionRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/player/ai")
@RequiredArgsConstructor
@Validated
public class AiTestController {

    private final AIService aiService;

    @PostMapping("/generate-situation")
    public ResponseEntity<String> generateSituation(@Valid @RequestBody GenerateSituationRequest request) {
        String content = aiService.generateStoryEnigmaticSituation(
            request.title(),
            request.genre(),
            request.difficulty()
        );
        return ResponseEntity.ok(content);
    }

    @PostMapping("/generate-solution")
    public ResponseEntity<String> generateSolution(@Valid @RequestBody GenerateSolutionRequest request) {
        String content = aiService.generateStoryFullSolution(
            request.title(),
            request.enigmaticSituation()
        );
        return ResponseEntity.ok(content);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<Boolean> evaluateStory(@Valid @RequestBody EvaluateStoryRequest request) {
        Boolean isApproved = aiService.storyEvaluation(
            request.enigmaticSituation(),
            request.fullSolution()
        );
        return ResponseEntity.ok(isApproved);
    }
}
