package com.example.vestigioapi.application.trivia.ai;

import java.util.List;
import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.framework.ai.AIIntegrationService;
import com.example.vestigioapi.application.trivia.question.Question;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TriviaAIService {

    private final AIIntegrationService aiIntegrationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public Question generateQuestionEntity(String category, String difficulty) {
        String json = generateQuestion(category, difficulty);
        try {
            JsonNode root = objectMapper.readTree(json);
            String text = root.path("text").asText("");
            JsonNode optionsNode = root.path("options");
            List<String> options = new ArrayList<>();
            if (optionsNode.isArray()) {
                optionsNode.forEach(n -> options.add(n.asText("")));
            }
            int correctIndex = root.path("correctIndex").asInt(0);
            String explanation = root.path("explanation").asText("");

            Question q = new Question();
            q.setText(text);
            q.setOptions(options);
            q.setCorrectAnswerIndex(correctIndex);
            q.setExplanation(explanation);
            return q;
        } catch (Exception e) {
            Question q = new Question();
            q.setText("Pergunta gerada pela IA não pôde ser interpretada.");
            q.setOptions(List.of("Opção A", "Opção B", "Opção C", "Opção D"));
            q.setCorrectAnswerIndex(0);
            q.setExplanation("Falha ao interpretar JSON da IA.");
            return q;
        }
    }
}
