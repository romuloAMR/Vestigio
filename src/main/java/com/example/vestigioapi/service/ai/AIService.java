package com.example.vestigioapi.service.ai;

import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;
import com.example.vestigioapi.util.StoryPromptTemplates;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    @PostConstruct
    public void init() {
        this.chatClient = chatClientBuilder.build();
    }
    
    public String generateStoryEnigmaticSituation(String title, Genre genre, Difficulty difficulty) {

        String userPromptContent = String.format(
            StoryPromptTemplates.ENIGMATIC_SITUATION_PROMPT,
            title,
            genre.name(),
            difficulty.name()
        );
        
        String result = chatClient.prompt()
            .user(userPromptContent)
            .call()
            .content();

        return cleanMarkdown(result);
    }

    public String generateStoryFullSolution(String title, String situation) {
        
        String userPromptContent = String.format(
            StoryPromptTemplates.FULL_SOLUTION_PROMPT,
            title,
            situation
        );
        
        String result = chatClient.prompt()
            .user(userPromptContent)
            .call()
            .content();

        return cleanMarkdown(result);
    }

    public Boolean storyEvaluation(String enigmaticSituation, String fullSolution) {
        
        String userPromptContent = String.format(
            StoryPromptTemplates.STORY_EVALUATION,
            enigmaticSituation,
            fullSolution
        );
        
        String result = chatClient.prompt()
            .user(userPromptContent)
            .call()
            .content();

        System.out.println("String evaluation " + result);
        return result.toLowerCase().contains("true");
    }

    private String cleanMarkdown(String markdownText) {
        if (markdownText == null || markdownText.isEmpty()) {
            return "";
        }

        String cleanedText = markdownText;

        cleanedText = cleanedText.replaceAll("^\\s*#+\\s*.*\\n?", "");
        cleanedText = cleanedText.replaceAll("[*_]{1,2}", "");
        cleanedText = cleanedText.replaceAll("^[\\s]*[-*+]\\s", "");
        cleanedText = cleanedText.replaceAll("^[\\s]*\\d+\\.\\s", "");
        cleanedText = cleanedText.replaceAll("^>\\s*", "");
        cleanedText = cleanedText.replaceAll("^[-*]{3,}\\n?", "");
        cleanedText = cleanedText.replaceAll("(?m)^\\s*$\\n", "");
        cleanedText = cleanedText.trim();

        return cleanedText;
    }
}
