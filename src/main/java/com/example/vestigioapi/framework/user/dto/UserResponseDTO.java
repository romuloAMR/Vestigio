package com.example.vestigioapi.framework.user.dto;

import java.time.LocalDateTime;

import com.example.vestigioapi.framework.user.model.Role;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    Role role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
