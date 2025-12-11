package com.example.vestigioapi.framework.ai;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Serviço centralizado de IA usando Spring AI + Vertex AI Gemini.
 * Este serviço é compartilhado por todos os jogos da plataforma.
 * 
 * Responsabilidades:
 * - Interface com Vertex AI Gemini
 * - Geração de conteúdo para todos os jogos
 * - Validação de conteúdo
 * - Limpeza de respostas
 */
@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final ChatClient.Builder chatClientBuilder;
    private ChatClient chatClient;

    @PostConstruct
    public void init() {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Executa um prompt customizado no Vertex AI
     */
    public String executePrompt(String prompt) {
        String result = chatClient.prompt()
            .user(prompt)
            .call()
            .content();
        return cleanMarkdown(result);
    }

    /**
     * Executa um prompt sem limpeza de markdown
     */
    public String executePromptRaw(String prompt) {
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    /**
     * Limpeza de markdown de respostas da IA
     */
    private String cleanMarkdown(String markdownText) {
        if (markdownText == null || markdownText.isEmpty()) {
            return "";
        }

        String cleanedText = markdownText;
        cleanedText = cleanedText.replaceAll("^\\s*#+\\s*.*\\n?", "");
        cleanedText = cleanedText.replaceAll("[*_]{1,2}", "");
        cleanedText = cleanedText.replaceAll("^[\\s]*[-*+]\\s", "");
        cleanedText = cleanedText.replaceAll("^[\\s]*\\d+\\.\\s", "");
        cleanedText = cleanedText.replaceAll("^>\\s*", "");
        cleanedText = cleanedText.replaceAll("^[-*]{3,}\\n?", "");
        cleanedText = cleanedText.replaceAll("(?m)^\\s*$\\n", "");
        cleanedText = cleanedText.trim();

        return cleanedText;
    }
}
