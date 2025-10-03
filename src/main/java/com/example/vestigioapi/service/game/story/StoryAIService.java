package com.example.vestigioapi.service.game.story;

import com.example.vestigioapi.dto.game.story.StoryAICreateDTO;
import com.example.vestigioapi.dto.game.story.StoryCreateDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.util.StoryPromptTemplates;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoryAIService {

    private final StoryService storyService;
    private final ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    @PostConstruct
    public void init() {
        this.chatClient = chatClientBuilder.build();
    }

    public StoryResponseDTO createAIStory(StoryAICreateDTO dto, User creator) {
        
        String title = dto.title();
        Genre genre = dto.genre();
        Difficulty difficulty = dto.difficulty();
        String enigmaticSituation = generateEnigmaticSituation(title, genre, difficulty);
        String fullSolution = generateFullSolution(title, enigmaticSituation);

        StoryCreateDTO fullStoryDTO = new StoryCreateDTO(
            title,
            enigmaticSituation,
            fullSolution,
            genre,
            difficulty
        );
        
        return storyService.createStory(fullStoryDTO, creator);
    }
    
    private String generateEnigmaticSituation(String title, Genre genre, Difficulty difficulty) {

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

    private String generateFullSolution(String title, String situation) {
        
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
