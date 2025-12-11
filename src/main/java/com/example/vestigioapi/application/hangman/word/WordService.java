package com.example.vestigioapi.application.hangman.word;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.hangman.word.dto.WordCreateDTO;
import com.example.vestigioapi.application.hangman.word.dto.WordResponseDTO;
import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;
import com.example.vestigioapi.application.hangman.ai.HangmanAIService;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final HangmanAIService hangmanAIService;
    
    public WordResponseDTO createWord(WordCreateDTO dto, User creator) {

        Word word = new Word();
        word.setName(dto.name());
        word.setType(dto.type());
        word.setDifficulty(dto.difficulty());
        word.setCreator(creator);

        Word savedWord = wordRepository.save(word);

        return toResponseDTO(savedWord);
    }

    public WordResponseDTO getWordById(Long id) {
        Word story = wordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Palavra não encontrada com id: " + id));
        return toResponseDTO(story);
    }

    public List<WordResponseDTO> getWordsByCreator(User creator) {
        return wordRepository.findByCreator(creator)
            .stream()
            .map(this::toResponseDTO)
            .toList();
    }

    public List<WordResponseDTO> findRandomWords(int count) {
        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        int desired = Math.max(0, count);
        int limit = Math.min(desired, allWords.size());
        
        return allWords.stream()
            .limit(limit)
            .map(this::toResponseDTO)
            .toList();
    }

    public String generateAIWord(String category, String difficulty) {
        try {
            Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
            return hangmanAIService.generateWord(category, diff);
        } catch (IllegalArgumentException e) {
            return hangmanAIService.generateWord(category, Difficulty.EASY);
        }
    }

    public String getHintForWord(String word, int wrongGuesses) {
        return hangmanAIService.generateHint(word, wrongGuesses);
    }

    public WordResponseDTO updateWord(Long id, WordCreateDTO dto, User creator) {
        Word word = wordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Palavra não encontrada com id: " + id));

        validateWordOwnership(word, creator);
        
        word.setName(dto.name());
        word.setType(dto.type());
        word.setDifficulty(dto.difficulty());
        Word updatedWord = wordRepository.save(word);
        return toResponseDTO(updatedWord);
    }

    public void deleteWord(Long id, User creator) {
        Word word = wordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Palavra não encontrada com id: " + id));
        
        validateWordOwnership(word, creator);

        wordRepository.delete(word);
    }

    private void validateWordOwnership(Word word, User creator) {
        if (word.getCreator() == null || !word.getCreator().getId().equals(creator.getId())) {
            throw new ForbiddenActionException("Você não tem permissão para essa ação com a palavra");
        }
    }

    private WordResponseDTO toResponseDTO(Word word) {
        return new WordResponseDTO(
            word.getId(),
            word.getName(),
            word.getType(),
            word.getDifficulty(),
            word.getCreator() != null ? word.getCreator().getName() : "System"
        );
    }
}