package com.example.vestigioapi.vestigio.dto;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequestDTO(
    @NotBlank String questionText
) {}