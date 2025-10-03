package com.example.vestigioapi.controller.game.story;

import com.example.vestigioapi.dto.game.story.StoryAICreateDTO;
import com.example.vestigioapi.dto.game.story.StoryCreateDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.service.game.story.StoryAIService;
import com.example.vestigioapi.service.game.story.StoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/api/player/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;
    private final StoryAIService storyAIService;

    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(
        @RequestBody StoryCreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        StoryResponseDTO response = storyService.createStory(dto, creator);
        return ResponseEntity.created(URI.create("/api/player/stories/" + response.id())).body(response);
    }

    @PostMapping("/ai")
    public ResponseEntity<StoryResponseDTO> createStoryWithAI(
        @RequestBody StoryAICreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        StoryResponseDTO response = storyAIService.createAIStory(dto, creator);
        return ResponseEntity.created(URI.create("/api/player/stories/ai/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseDTO> getStoryById(@PathVariable Long id) {
        StoryResponseDTO response = storyService.getStoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseDTO>> getAllStories() {
        List<StoryResponseDTO> stories = storyService.getAllStories();
        return ResponseEntity.ok(stories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryResponseDTO> updateStory(@PathVariable Long id, @RequestBody StoryCreateDTO dto) {
        StoryResponseDTO response = storyService.updateStory(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }
}
