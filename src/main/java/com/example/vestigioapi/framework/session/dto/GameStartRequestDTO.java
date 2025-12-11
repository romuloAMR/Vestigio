package com.example.vestigioapi.framework.session.dto;

import java.util.Map;

public record GameStartRequestDTO(
    String gameType,
    Map<String, Object> configParams
) {}
