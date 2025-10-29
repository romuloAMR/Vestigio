package com.example.vestigioapi.service.ai;

import com.example.vestigioapi.dto.game.story.StoryAICreateDTO;
import com.example.vestigioapi.util.StoryPromptTemplates;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AIService {
    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final Client geminiClient;

    @Value("${application.ai.gemini.model:gemini-2.0-flash-exp}")
    private String modelName;

    public AIService(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    @PostConstruct
    private void init() {
        log.info("AIService initialized with Gemini model: {}", modelName);
    }

    public String generateStoryEnigmaticSituation(StoryAICreateDTO createDTO) {
        log.info("Generating enigmatic situation for title: {}, genre: {}, difficulty: {}", 
                 createDTO.title(), createDTO.genre(), createDTO.difficulty());

        String prompt = String.format(
            StoryPromptTemplates.ENIGMATIC_SITUATION_PROMPT,
            createDTO.title(),
            createDTO.genre().name(),
            createDTO.difficulty().name()
        );

        GenerateContentResponse response = geminiClient.models.generateContent(
            modelName,
            prompt,
            null
        );

        String generatedText = response.text();
        
        log.info("Generated enigmatic situation with {} characters", 
                 generatedText != null ? generatedText.length() : 0);
        
        return generatedText;
    }

    public String generateFullSolution(String title, String enigmaticSituation) {
        log.info("Generating full solution for title: {}", title);

        String prompt = String.format(
            StoryPromptTemplates.FULL_SOLUTION_PROMPT,
            title,
            enigmaticSituation
        );

        GenerateContentResponse response = geminiClient.models.generateContent(
            modelName,
            prompt,
            null
        );

        String generatedText = response.text();
        
        log.info("Generated full solution with {} characters", 
                 generatedText != null ? generatedText.length() : 0);
        
        return generatedText;
    }
}
