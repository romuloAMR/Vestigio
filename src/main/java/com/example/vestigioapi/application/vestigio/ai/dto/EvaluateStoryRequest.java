package com.example.vestigioapi.application.vestigio.ai.dto;

import com.example.vestigioapi.framework.common.util.ValidationMessages;

import jakarta.validation.constraints.NotBlank;

public record EvaluateStoryRequest(
    @NotBlank(message = ValidationMessages.ENIGMATIC_SITUATION_REQUIRED)
    String enigmaticSituation,

    @NotBlank(message = ValidationMessages.FULL_SOLUTION_REQUIRED)
    String fullSolution
) {}
