package com.example.vestigioapi.framework.session.dto;

import java.util.Map;

public record GameActionRequestDTO(
    String actionType,
    Map<String, Object> payload
) {}
