package com.example.vestigioapi.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.dto.user.PasswordUpdateDTO;
import com.example.vestigioapi.dto.user.UserResponseDTO;
import com.example.vestigioapi.dto.user.UserUpdateRequestDTO;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO updateUserDetails(Long userId, UserUpdateRequestDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

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
                    throw new RuntimeException("Email já está em uso.");
                }
                user.setEmail(dto.email());
                changed = true;
            }
        }

        User finalUser = user;

        if (changed) {
            userRepository.save(user);
            
            userRepository.flush();
            finalUser = userRepository.findById(userId).orElseThrow(); 
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
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new RuntimeException("A senha atual está incorreta."); 
        }
        
        if (!dto.newPassword().equals(dto.confirmationPassword())) {
            throw new RuntimeException("A nova senha e a confirmação não coincidem.");
        }
        
        if (passwordEncoder.matches(dto.newPassword(), user.getPassword())) {
            throw new RuntimeException("A nova senha deve ser diferente da senha atual.");
        }

        String encodedNewPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(encodedNewPassword);
        
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId, String providedPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
            throw new RuntimeException("A senha fornecida está incorreta. A exclusão foi cancelada."); 
        }

        userRepository.delete(user);
    }
}