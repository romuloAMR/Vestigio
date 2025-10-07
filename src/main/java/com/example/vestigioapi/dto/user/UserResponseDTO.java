package com.example.vestigioapi.dto.user;

import java.time.LocalDateTime;

import com.example.vestigioapi.model.user.Role;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    Role role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}