package com.example.vestigioapi.service.game.story;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.story.StoryCreateDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.game.story.StoryStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.StoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    
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

    public StoryResponseDTO getStoryById(Long id) {
        Story story = storyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Story not found with id: " + id));
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
            .orElseThrow(() -> new IllegalArgumentException("Story not found with id: " + id));
        story.setTitle(dto.title());
        story.setEnigmaticSituation(dto.enigmaticSituation());
        story.setFullSolution(dto.fullSolution());
        story.setGenre(dto.genre());
        story.setDifficulty(dto.difficulty());
        Story updatedStory = storyRepository.save(story);
        return toResponseDTO(updatedStory);
    }

    public void deleteStory(Long id) {
        if (!storyRepository.existsById(id)) {
            throw new IllegalArgumentException("Story not found with id: " + id);
        }
        storyRepository.deleteById(id);
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