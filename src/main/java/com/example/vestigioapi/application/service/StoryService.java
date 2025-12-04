package com.example.vestigioapi.application.service;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.dto.StoryAICreateDTO;
import com.example.vestigioapi.application.dto.StoryCreateDTO;
import com.example.vestigioapi.application.dto.StoryResponseDTO;
import com.example.vestigioapi.application.model.story.Difficulty;
import com.example.vestigioapi.application.model.story.Genre;
import com.example.vestigioapi.application.model.story.Story;
import com.example.vestigioapi.application.model.story.StoryStatus;
import com.example.vestigioapi.application.repository.StoryRepository;
import com.example.vestigioapi.application.util.VestigioErrorMessages;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final AIService aiService;
    
    public StoryResponseDTO createStory(StoryCreateDTO dto, User creator) {

        Boolean isDangerous = aiService.storyEvaluation(dto.enigmaticSituation(), dto.fullSolution());

        System.out.println("Danger: " + isDangerous);

        if (isDangerous){
            throw new BusinessRuleException(VestigioErrorMessages.STORY_REJECTED);
        }

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
            .orElseThrow(() -> new ResourceNotFoundException(VestigioErrorMessages.STORY_NOT_FOUND + " com id: " + id));
        return toResponseDTO(story);
    }

    public List<StoryResponseDTO> getStoriesByCreator(User creator) {
        return storyRepository.findByCreator(creator)
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

    public StoryResponseDTO updateStory(Long id, StoryCreateDTO dto, User creator) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(VestigioErrorMessages.STORY_NOT_FOUND + " com id: " + id));

        validateStoryOwnership(story, creator);
        
        story.setTitle(dto.title());
        story.setEnigmaticSituation(dto.enigmaticSituation());
        story.setFullSolution(dto.fullSolution());
        story.setGenre(dto.genre());
        story.setDifficulty(dto.difficulty());
        Story updatedStory = storyRepository.save(story);
        return toResponseDTO(updatedStory);
    }

    public void deleteStory(Long id, User creator) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(VestigioErrorMessages.STORY_NOT_FOUND + " com id: " + id));
        
        validateStoryOwnership(story, creator);

        storyRepository.delete(story);
    }

    private void validateStoryOwnership(Story story, User creator) {
        if (story.getCreator() == null || !story.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenActionException(VestigioErrorMessages.UNAUTHORIZED_ACTION_IN_STORY);
        }
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