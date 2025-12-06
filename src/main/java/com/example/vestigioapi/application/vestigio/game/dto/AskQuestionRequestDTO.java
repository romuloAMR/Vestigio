package com.example.vestigioapi.application.vestigio.game.dto;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequestDTO(
    @NotBlank String questionText
) {}