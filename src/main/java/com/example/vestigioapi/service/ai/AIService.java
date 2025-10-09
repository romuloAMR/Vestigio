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
        
        return chatClient.prompt()
            .user(userPromptContent)
            .call()
            .content();
    }

    public String generateStoryFullSolution(String title, String situation) {
        
        String userPromptContent = String.format(
            StoryPromptTemplates.FULL_SOLUTION_PROMPT,
            title,
            situation
        );
        
        return chatClient.prompt()
            .user(userPromptContent)
            .call()
            .content();
    }
}
