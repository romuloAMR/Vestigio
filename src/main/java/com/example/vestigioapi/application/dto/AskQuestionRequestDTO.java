package com.example.vestigioapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record AskQuestionRequestDTO(
    @NotBlank String questionText
) {}