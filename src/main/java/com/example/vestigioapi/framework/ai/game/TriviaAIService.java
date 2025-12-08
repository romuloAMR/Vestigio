package com.example.vestigioapi.framework.ai.game;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.framework.ai.AIIntegrationService;

/**
 * Serviço de IA especializado para o jogo Trivia (Quiz).
 * Gerencia geração de perguntas e respostas do IA.
 */
@Service
@RequiredArgsConstructor
public class TriviaAIService {

    private final AIIntegrationService aiIntegrationService;

    public String generateQuestion(String category, String difficulty) {
        String prompt = String.format(
            "Gere uma pergunta de múltipla escolha para um quiz de conhecimento geral.\n" +
            "Categoria: %s\n" +
            "Dificuldade: %s\n" +
            "Retorne em formato JSON com os campos:\n" +
            "{\n" +
            "  \"text\": \"pergunta aqui\",\n" +
            "  \"options\": [\"opção1\", \"opção2\", \"opção3\", \"opção4\"],\n" +
            "  \"correctIndex\": 0,\n" +
            "  \"explanation\": \"por que essa é a resposta correta\"\n" +
            "}",
            category, difficulty
        );
        return aiIntegrationService.executePromptRaw(prompt);
    }

    public Integer getAIAnswer(String questionText, List<String> options) {
        StringBuilder prompt = new StringBuilder(
            "Você é um especialista. Responda a pergunta corretamente.\n" +
            "Pergunta: " + questionText + "\n" +
            "Opções:\n"
        );

        for (int i = 0; i < options.size(); i++) {
            prompt.append(i).append(") ").append(options.get(i)).append("\n");
        }

        prompt.append("Retorne APENAS o número (0, 1, 2 ou 3) da resposta correta.");

        String result = aiIntegrationService.executePromptRaw(prompt.toString());
        try {
            return Integer.parseInt(result.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
