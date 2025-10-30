package com.example.vestigioapi.service.game.story;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.story.StoryAICreateDTO;
import com.example.vestigioapi.dto.game.story.StoryCreateDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.exception.ResourceNotFoundException;
import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.game.story.StoryStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.StoryRepository;
import com.example.vestigioapi.service.ai.AIService;
import com.example.vestigioapi.util.ErrorMessages;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final AIService aiService;
    
    public StoryResponseDTO createStory(StoryCreateDTO dto, User creator) {
        Story story = new Story();
        story.setTitle(dto.title());
        story.setEnigmaticSituation(dto.enigmaticSituation());
        story.setFullSolution(dto.fullSolution());
        story.setGenre(dto.genre());
        story.setDifficulty(dto.difficulty());
        story.setCreator(creator);
        story.setStatus(StoryStatus.PENDING);

        Story savedStory = storyRepository.save(story);

        return toResponseDTO(savedStory);
    }

    public StoryResponseDTO createAIStory(StoryAICreateDTO dto, User creator) {
        
        String title = dto.title();
        Genre genre = dto.genre();
        Difficulty difficulty = dto.difficulty();
        String enigmaticSituation = aiService.generateStoryEnigmaticSituation(title, genre, difficulty);
        String fullSolution = aiService.generateStoryFullSolution(title, enigmaticSituation);

        StoryCreateDTO fullStoryDTO = new StoryCreateDTO(
            title,
            enigmaticSituation,
            fullSolution,
            genre,
            difficulty
        );
        
        return createStory(fullStoryDTO, creator);
    }

    public StoryResponseDTO getStoryById(Long id) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND + " com id: " + id));
        return toResponseDTO(story);
    }

    public List<StoryResponseDTO> getAllStories() {
        return storyRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public List<StoryResponseDTO> findRandomStories(int count) {
        List<Story> allStories = storyRepository.findAll();
        Collections.shuffle(allStories);

        int desired = Math.max(0, count);
        List<StoryResponseDTO> result = new java.util.ArrayList<>();

        int takeFromDb = Math.min(2, allStories.size());
        for (int i = 0; i < takeFromDb && result.size() < desired; i++) {
            result.add(toResponseDTO(allStories.get(i)));
        }

        if (desired > result.size()) {
           Genre genre = Genre.COMEDY;
            Difficulty difficulty = Difficulty.EASY;
            if (!allStories.isEmpty()) {
                Story reference = allStories.get(0);
                if (reference.getGenre() != null) genre = reference.getGenre();
                if (reference.getDifficulty() != null) difficulty = reference.getDifficulty();
            }

            String title = "História gerada - " + System.currentTimeMillis();
            StoryAICreateDTO aiDto = new StoryAICreateDTO(title, genre, difficulty);
            StoryResponseDTO aiStory = createAIStory(aiDto, null);
            result.add(aiStory);
        }

        int idx = takeFromDb;
        while (result.size() < desired && idx < allStories.size()) {
            result.add(toResponseDTO(allStories.get(idx)));
            idx++;
        }

        return result;
    }

    public StoryResponseDTO updateStory(Long id, StoryCreateDTO dto) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND + " com id: " + id));
        story.setTitle(dto.title());
        story.setEnigmaticSituation(dto.enigmaticSituation());
        story.setFullSolution(dto.fullSolution());
        story.setGenre(dto.genre());
        story.setDifficulty(dto.difficulty());
        Story updatedStory = storyRepository.save(story);
        return toResponseDTO(updatedStory);
    }

    public void deleteStory(Long id) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND + " com id: " + id));
        
        storyRepository.delete(story);
    }

    private StoryResponseDTO toResponseDTO(Story story) {
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
}