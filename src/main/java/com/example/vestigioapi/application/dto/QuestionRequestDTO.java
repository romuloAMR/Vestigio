package com.example.vestigioapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record QuestionRequestDTO(
    @NotBlank(message = "Question text is required")
    String question
) {}
