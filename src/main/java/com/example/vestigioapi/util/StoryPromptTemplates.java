package com.example.vestigioapi.util;

public class StoryPromptTemplates {
    public static final String ENIGMATIC_SITUATION_PROMPT = """
        Gere uma SITUAÇÃO ENIGMÁTICA (apenas o texto da situação).
        - Título do Mistério: "%s"
        - Gênero: %s
        - Dificuldade: %s.
        - Restrição: A resposta deve conter APENAS o texto do enigma, sem títulos, introduções ou explicações.
        - Restrição: O texto deve ter MENOS de 60 palavras.
        """;

    public static final String FULL_SOLUTION_PROMPT = """
        Com base na seguinte SITUAÇÃO ENIGMÁTICA, gere a SOLUÇÃO COMPLETA.
        - Título do Mistério: "%s"
        - Situação Enigmática: "%s"
        - Restrição: A resposta deve conter APENAS o texto da solução detalhada.
        - Restriçao: O texto deve ser objetivo, sem prolixidade.
        """;
}
