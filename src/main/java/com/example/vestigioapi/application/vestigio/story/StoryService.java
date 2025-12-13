package com.example.vestigioapi.application.vestigio.story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;
import com.example.vestigioapi.application.vestigio.story.constants.Genre;
import com.example.vestigioapi.application.vestigio.story.constants.StoryStatus;
import com.example.vestigioapi.application.vestigio.story.dto.StoryAICreateDTO;
import com.example.vestigioapi.application.vestigio.story.dto.StoryCreateDTO;
import com.example.vestigioapi.application.vestigio.story.dto.StoryResponseDTO;
import com.example.vestigioapi.application.vestigio.util.VestigioErrorMessages;
import com.example.vestigioapi.application.vestigio.ai.VestigioAIService;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final VestigioAIService vestigioAIService;
    
    public StoryResponseDTO createStory(StoryCreateDTO dto, User creator) {

        System.out.println("[StoryService] Starting content evaluation for story: " + dto.title());
        
        Boolean isDangerous = vestigioAIService.evaluateStoryContent(dto.enigmaticSituation(), dto.fullSolution());

        System.out.println("[StoryService] Content evaluation result - Is dangerous: " + isDangerous);

        if (isDangerous){
            System.out.println("[StoryService] Story rejected due to inappropriate content");
            throw new BusinessRuleException(VestigioErrorMessages.STORY_REJECTED);
        }
        
        System.out.println("[StoryService] Story approved, proceeding with creation");

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
        String enigmaticSituation = vestigioAIService.generateEnigmaticSituation(title, genre, difficulty);
        String fullSolution = vestigioAIService.generateFullSolution(title, enigmaticSituation);

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
        List<Story> randomStories = storyRepository.findRandomStories(count);
        
        List<StoryResponseDTO> result = new ArrayList<>(randomStories.stream()
                .map(this::toResponseDTO)
                .toList());

        if (result.size() < count) {
            Genre genre = Genre.DRAMA;
            Difficulty difficulty = Difficulty.EASY;

            if (!randomStories.isEmpty()) {
                Story ref = randomStories.get(0);
                if (ref.getGenre() != null) genre = ref.getGenre();
                if (ref.getDifficulty() != null) difficulty = ref.getDifficulty();
            }

            String title = "Mistério Gerado - " + System.currentTimeMillis();
            StoryAICreateDTO aiDto = new StoryAICreateDTO(title, genre, difficulty);

            StoryResponseDTO aiStory = createAIStory(aiDto, null);
            result.add(aiStory);
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