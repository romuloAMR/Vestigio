package com.example.vestigioapi.framework.ai.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.framework.ai.AIIntegrationService;
import com.example.vestigioapi.application.vestigio.story.constants.Difficulty;
import com.example.vestigioapi.application.vestigio.story.constants.Genre;

/**
 * Serviço de IA especializado para o jogo Vestigio.
 * Gerencia toda a geração de histórias enigmáticas.
 */
@Service
@RequiredArgsConstructor
public class VestigioAIService {

    private final AIIntegrationService aiIntegrationService;

    public String generateEnigmaticSituation(String title, Genre genre, Difficulty difficulty) {
        String prompt = String.format(
            "Crie uma situação misteriosa e enigmática para um jogo de dedução.\n" +
            "Título: %s\n" +
            "Gênero: %s\n" +
            "Dificuldade: %s\n" +
            "A situação deve ter 150-300 palavras, ser envolvente e deixar o jogador intrigado.",
            title, genre.name(), difficulty.name()
        );
        return aiIntegrationService.executePrompt(prompt);
    }

    public String generateFullSolution(String title, String enigmaticSituation) {
        String prompt = String.format(
            "Crie uma solução completa e satisfatória para esta situação enigmática.\n" +
            "Título: %s\n" +
            "Situação: %s\n" +
            "A solução deve:\n" +
            "1. Ser coerente com a situação\n" +
            "2. Ser surpreendente mas lógica\n" +
            "3. Ter 200-400 palavras",
            title, enigmaticSituation
        );
        return aiIntegrationService.executePrompt(prompt);
    }

    public boolean evaluateStoryContent(String enigmaticSituation, String fullSolution) {
        try {
            String prompt = String.format(
                "Avalie se o seguinte conteúdo é SEGURO e APROPRIADO para um jogo educativo.\n\n" +
                "Situação: %s\n\n" +
                "Solução: %s\n\n" +
                "IMPORTANTE: Responda APENAS com uma palavra:\n" +
                "- SEGURO: se o conteúdo for apropriado\n" +
                "- PERIGOSO: se contiver violência explícita, conteúdo sexual, discriminação ou ódio\n\n" +
                "Resposta:",
                enigmaticSituation, fullSolution
            );
            
            String result = aiIntegrationService.executePromptRaw(prompt);
            String cleanResult = result.trim().toLowerCase();
            
            System.out.println("[AI Evaluation] Raw response: " + result);
            System.out.println("[AI Evaluation] Clean response: " + cleanResult);
            
            // Retorna true se for PERIGOSO (para manter compatibilidade com a lógica existente)
            boolean isDangerous = cleanResult.contains("perigoso") || 
                                  cleanResult.contains("dangerous") ||
                                  (!cleanResult.contains("seguro") && !cleanResult.contains("safe"));
            
            System.out.println("[AI Evaluation] Is dangerous: " + isDangerous);
            return isDangerous;
            
        } catch (Exception e) {
            System.err.println("[AI Evaluation] Error during content evaluation: " + e.getMessage());
            e.printStackTrace();
            // Em caso de erro, permite o conteúdo passar (fail-open)
            return false;
        }
    }
}
