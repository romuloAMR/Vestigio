package com.example.vestigioapi.application.story;

import com.example.vestigioapi.application.story.dto.StoryAICreateDTO;
import com.example.vestigioapi.application.story.dto.StoryCreateDTO;
import com.example.vestigioapi.application.story.dto.StoryResponseDTO;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/api/v1/player/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public ResponseEntity<StoryResponseDTO> createStory(
        @Valid @RequestBody StoryCreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        StoryResponseDTO response = storyService.createStory(dto, creator);
        return ResponseEntity
            .created(URI.create("/api/v1/player/stories/" + response.id()))
            .body(response);
    }

    @PostMapping("/ai")
    public ResponseEntity<StoryResponseDTO> createStoryWithAI(
        @Valid @RequestBody StoryAICreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        StoryResponseDTO response = storyService.createAIStory(dto, creator);
        return ResponseEntity
            .created(URI.create("/api/v1/player/stories/ai/" + response.id()))
            .body(response);
    }

    @GetMapping("/random")
    public ResponseEntity<List<StoryResponseDTO>> getRandomStories(
            @RequestParam(defaultValue = "3") int count) {
        List<StoryResponseDTO> stories = storyService.findRandomStories(count);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseDTO> getStoryById(@PathVariable Long id) {
        StoryResponseDTO response = storyService.getStoryById(id);
        return ResponseEntity
            .ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseDTO>> getMyStories(
        @AuthenticationPrincipal User creator
    ) {
        List<StoryResponseDTO> stories = storyService.getStoriesByCreator(creator);
        return ResponseEntity
            .ok(stories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryResponseDTO> updateStory(
        @PathVariable Long id,
        @Valid @RequestBody StoryCreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        StoryResponseDTO response = storyService.updateStory(id, dto, creator);
        return ResponseEntity
            .ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(
        @PathVariable Long id,
        @AuthenticationPrincipal User creator
    ) {
        storyService.deleteStory(id, creator);
        return ResponseEntity
            .noContent()
            .build();
    }
}
