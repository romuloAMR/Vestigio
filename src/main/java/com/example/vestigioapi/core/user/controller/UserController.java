package com.example.vestigioapi.core.user.controller;

import com.example.vestigioapi.core.user.dto.PasswordUpdateDTO;
import com.example.vestigioapi.core.user.dto.UserDeletionDTO;
import com.example.vestigioapi.core.user.dto.UserResponseDTO;
import com.example.vestigioapi.core.user.dto.UserUpdateRequestDTO;
import com.example.vestigioapi.core.user.model.User;
import com.example.vestigioapi.core.user.service.UserService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal User currentUser) {
        UserResponseDTO response = new UserResponseDTO(
            currentUser.getId(),
            currentUser.getName(),
            currentUser.getEmail(),
            currentUser.getRole(),
            currentUser.getCreatedAt(),
            currentUser.getUpdatedAt()
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserUpdateRequestDTO request
    ) {
        UserResponseDTO updatedUser = userService.updateUserDetails(currentUser.getId(), request);
        return ResponseEntity
            .ok(updatedUser);
    }
    
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PasswordUpdateDTO request
    ) {
        userService.updatePassword(currentUser.getId(), request);
        return ResponseEntity
            .noContent()
            .build(); 
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UserDeletionDTO request
    ) {
        userService.deleteUser(currentUser.getId(), request.currentPassword());
        return ResponseEntity
            .noContent() 
            .build();
    }
    
}
