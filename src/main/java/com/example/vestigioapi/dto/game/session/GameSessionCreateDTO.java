package com.example.vestigioapi.dto.game.session;

import jakarta.validation.constraints.NotNull;

public record GameSessionCreateDTO(@NotNull Long storyId) {}
