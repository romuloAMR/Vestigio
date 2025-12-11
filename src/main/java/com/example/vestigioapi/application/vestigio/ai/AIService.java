package com.example.vestigioapi.application.vestigio.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;
import com.example.vestigioapi.application.vestigio.story.constants.Genre;
import com.example.vestigioapi.framework.ai.game.VestigioAIService;
import com.example.vestigioapi.framework.ai.game.HangmanAIService;
import com.example.vestigioapi.framework.ai.game.TriviaAIService;

/**
 * @deprecated Use framework.ai.game.* services instead
 * 
 * Este serviço foi mantido por compatibilidade com código antigo.
 * Novas implementações devem usar diretamente:
 * - VestigioAIService para Vestigio
 * - HangmanAIService para Hangman
 * - TriviaAIService para Trivia
 */
@Service
@RequiredArgsConstructor
@Deprecated(forRemoval = true, since = "2.0")
public class AIService {

    private final VestigioAIService vestigioAIService;
    private final HangmanAIService hangmanAIService;
    private final TriviaAIService triviaAIService;

    // VESTIGIO METHODS
    public String generateStoryEnigmaticSituation(String title, Genre genre, Difficulty difficulty) {
        return vestigioAIService.generateEnigmaticSituation(title, genre, difficulty);
    }

    public String generateStoryFullSolution(String title, String situation) {
        return vestigioAIService.generateFullSolution(title, situation);
    }

    public Boolean storyEvaluation(String enigmaticSituation, String fullSolution) {
        return vestigioAIService.evaluateStoryContent(enigmaticSituation, fullSolution);
    }

    // HANGMAN METHODS
    public String generateHangmanWord(String category, Difficulty difficulty) {
        return hangmanAIService.generateWord(category, difficulty);
    }

    public String generateHangmanHint(String word, int wrongGuesses) {
        return hangmanAIService.generateHint(word, wrongGuesses);
    }

    public Boolean validateHangmanGuess(String word, char guess) {
        return hangmanAIService.isValidGuess(word, guess);
    }

    // TRIVIA METHODS
    public String generateTriviaQuestion(String category, String difficulty) {
        return triviaAIService.generateQuestion(category, difficulty);
    }

    public Integer getTriviaAIAnswer(String questionText, java.util.List<String> options) {
        return triviaAIService.getAIAnswer(questionText, options);
    }
}

