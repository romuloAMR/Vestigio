package com.example.vestigioapi.framework.ai;

/**
 * Documentação da Arquitetura de IA da Plataforma Vestigio
 * 
 * ESTRUTURA:
 * ─────────────────────────────────────────────────────────────────
 * framework/ai/
 * ├── AIIntegrationService          ← Centro de integração com Vertex AI
 * ├── game/
 * │   ├── VestigioAIService         ← Especializado em Vestigio
 * │   ├── HangmanAIService          ← Especializado em Hangman
 * │   └── TriviaAIService           ← Especializado em Trivia
 * └── prompt/
 *     ├── VestigioPrompts.java      ← Templates de Vestigio
 *     ├── HangmanPrompts.java       ← Templates de Hangman
 *     └── TriviaPrompts.java        ← Templates de Trivia
 * 
 * FLUXO DE CHAMADAS:
 * ─────────────────────────────────────────────────────────────────
 * 
 *   Game Service/Engine
 *        │
 *        ↓
 *   Specialized AI Service (e.g., VestigioAIService)
 *        │
 *        ↓
 *   AIIntegrationService (executa prompts)
 *        │
 *        ↓
 *   Spring AI + Vertex AI Gemini
 * 
 * RESPONSABILIDADES:
 * ─────────────────────────────────────────────────────────────────
 * 
 * AIIntegrationService:
 *   ✓ Configuração do ChatClient
 *   ✓ Execução de prompts no Vertex AI
 *   ✓ Limpeza de markdown nas respostas
 *   ✓ Tratamento de erros genéricos
 * 
 * Specialized AI Services (VestigioAIService, HangmanAIService, etc.):
 *   ✓ Lógica específica do jogo
 *   ✓ Validação de entrada
 *   ✓ Processamento de respostas
 *   ✓ Templates e prompts do jogo
 * 
 * CONFIGURAÇÃO:
 * ─────────────────────────────────────────────────────────────────
 * application.properties:
 *   spring.ai.vertex.ai.gemini.project-id=${VERTEX_AI_PROJECT_ID}
 *   spring.ai.vertex.ai.gemini.location=${VERTEX_AI_LOCATION}
 * 
 * Autenticação:
 *   Google ADC (Application Default Credentials) via GOOGLE_APPLICATION_CREDENTIALS
 * 
 * ESCALABILIDADE:
 * ─────────────────────────────────────────────────────────────────
 * 
 * Para adicionar novo jogo:
 * 1. Criar XYZAIService em framework/ai/game/
 * 2. Criar XYZPrompts.java em framework/ai/prompt/
 * 3. Injetar especializado AI Service no Game Service
 * 4. Pronto! Automaticamente integrável com Spring
 * 
 * MONITORAMENTO:
 * ─────────────────────────────────────────────────────────────────
 * 
 * Logging configurado:
 *   logging.level.com.example.vestigioapi.framework.ai=DEBUG
 */
public class AIArchitectureDocumentation {
    // This is a documentation file
}
