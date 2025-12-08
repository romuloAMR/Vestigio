package com.example.vestigioapi.application.hangman.word;

import com.example.vestigioapi.application.hangman.word.dto.WordCreateDTO;
import com.example.vestigioapi.application.hangman.word.dto.WordResponseDTO;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/api/v1/player/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping
    public ResponseEntity<WordResponseDTO> createWord(
        @Valid @RequestBody WordCreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        WordResponseDTO response = wordService.createWord(dto, creator);
        return ResponseEntity
            .created(URI.create("/api/v1/player/stories/" + response.id()))
            .body(response);
    }

    @GetMapping("/random")
    public ResponseEntity<List<WordResponseDTO>> getRandomWords(
            @RequestParam(defaultValue = "3") int count) {
        List<WordResponseDTO> words = wordService.findRandomWords(count);
        return ResponseEntity.ok(words);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WordResponseDTO> getWordById(@PathVariable Long id) {
        WordResponseDTO response = wordService.getWordById(id);
        return ResponseEntity
            .ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WordResponseDTO>> getMyWords(
        @AuthenticationPrincipal User creator
    ) {
        List<WordResponseDTO> words = wordService.getWordsByCreator(creator);
        return ResponseEntity
            .ok(words);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordResponseDTO> updateWord(
        @PathVariable Long id,
        @Valid @RequestBody WordCreateDTO dto,
        @AuthenticationPrincipal User creator
    ) {
        WordResponseDTO response = wordService.updateWord(id, dto, creator);
        return ResponseEntity
            .ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(
        @PathVariable Long id,
        @AuthenticationPrincipal User creator
    ) {
        wordService.deleteWord(id, creator);
        return ResponseEntity
            .noContent()
            .build();
    }
}
