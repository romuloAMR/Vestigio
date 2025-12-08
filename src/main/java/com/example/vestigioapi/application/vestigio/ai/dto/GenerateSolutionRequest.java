package com.example.vestigioapi.application.vestigio.ai.dto;

import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;

public record GenerateSolutionRequest(
    @NotBlank(message = ValidationMessages.TITLE_REQUIRED)
    String title,

    @NotBlank(message = ValidationMessages.ENIGMATIC_SITUATION_REQUIRED)
    String enigmaticSituation
) {}
