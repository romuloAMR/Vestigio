package com.example.vestigioapi.service.game.story;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.story.StoryAICreateDTO;
import com.example.vestigioapi.dto.game.story.StoryCreateDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.story.Difficulty;
import com.example.vestigioapi.model.game.story.Genre;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.game.story.StoryStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.StoryRepository;
import com.example.vestigioapi.service.ai.AIService;

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
            .orElseThrow(() -> new NoSuchElementException("Story not found with id: " + id));
        return toResponseDTO(story);
    }

    public List<StoryResponseDTO> getAllStories() {
        return storyRepository.findAll()
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public StoryResponseDTO updateStory(Long id, StoryCreateDTO dto) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Story not found with id: " + id));
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
            .orElseThrow(() -> new NoSuchElementException("Story not found with id: " + id));
        
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