package com.example.vestigioapi.framework.ai.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.framework.ai.AIIntegrationService;
import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;

/**
 * Serviço de IA especializado para o jogo Hangman (Forca).
 * Gerencia geração de palavras e dicas.
 */
@Service
@RequiredArgsConstructor
public class HangmanAIService {

    private final AIIntegrationService aiIntegrationService;

    public String generateWord(String category, Difficulty difficulty) {
        String prompt = String.format(
            "Gere uma palavra em português para o jogo da forca.\n" +
            "Categoria: %s\n" +
            "Dificuldade: %s\n" +
            "Retorne APENAS a palavra, sem explicações, sem acentos, em minúsculas.",
            category, difficulty.name()
        );
        String result = aiIntegrationService.executePromptRaw(prompt);
        return result.trim().toLowerCase().replaceAll("[^a-z]", "");
    }

    public String generateHint(String word, int wrongGuesses) {
        String prompt = String.format(
            "Dê uma dica criativa para a palavra: '%s'.\n" +
            "O jogador já errou %d vezes.\n" +
            "Dica deve ter até 50 caracteres, ser útil mas não revelar a resposta, em português.",
            word, wrongGuesses
        );
        return aiIntegrationService.executePrompt(prompt);
    }

    public boolean isValidGuess(String word, char guess) {
        return word.toLowerCase().contains(String.valueOf(guess).toLowerCase());
    }
}
