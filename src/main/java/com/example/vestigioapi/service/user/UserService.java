package com.example.vestigioapi.service.user;

import java.security.Principal;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.dto.user.PasswordUpdateDTO;
import com.example.vestigioapi.dto.user.UserResponseDTO;
import com.example.vestigioapi.dto.user.UserUpdateRequestDTO;
import com.example.vestigioapi.exception.BusinessRuleException;
import com.example.vestigioapi.exception.ResourceNotFoundException;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;
import com.example.vestigioapi.util.ErrorMessages;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO updateUserDetails(Long userId, UserUpdateRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        boolean changed = false;

        if (dto.name() != null && !dto.name().isBlank()) {
            if (!user.getName().equals(dto.name())) {
                user.setName(dto.name());
                changed = true;
            }
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            if (!user.getEmail().equals(dto.email())) {
                if (userRepository.findByEmail(dto.email()).isPresent()) {
                    throw new BusinessRuleException(ErrorMessages.EMAIL_ALREADY_EXISTS);
                }
                user.setEmail(dto.email());
                changed = true;
            }
        }

        User finalUser = user;

        if (changed) {
            userRepository.save(user);
            
            userRepository.flush();
            finalUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND)); 
        }

        return new UserResponseDTO(
            finalUser.getId(),
            finalUser.getName(),
            finalUser.getEmail(),
            finalUser.getRole(),
            finalUser.getCreatedAt(),
            finalUser.getUpdatedAt()
        );
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorMessages.INCORRECT_PASSWORD); 
        }
        
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new BusinessRuleException(ErrorMessages.NEW_PASSWORD_MISMATCH);
        }
        
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new BusinessRuleException(ErrorMessages.NEW_PASSWORD_IS_SAME);
        }

        String encodedNewPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedNewPassword);
        
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId, String providedPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
            throw new BadCredentialsException(ErrorMessages.DELETE_PASSWORD_INCORRECT); 
        }

        userRepository.delete(user);
    }

    public User getAuthenticatedUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new BusinessRuleException(ErrorMessages.USER_UNAUTHORISED);
        }
        
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }
}