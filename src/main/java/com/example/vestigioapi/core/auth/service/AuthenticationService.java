package com.example.vestigioapi.core.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.core.auth.dto.AuthenticationRequest;
import com.example.vestigioapi.core.auth.dto.AuthenticationResponse;
import com.example.vestigioapi.core.auth.dto.RegisterRequest;
import com.example.vestigioapi.core.common.exception.BusinessRuleException;
import com.example.vestigioapi.core.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.core.common.util.ErrorMessages;
import com.example.vestigioapi.core.user.model.Role;
import com.example.vestigioapi.core.user.model.User;
import com.example.vestigioapi.core.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        
        if (repository.findByEmail(request.email()).isPresent()) {
            throw new BusinessRuleException(ErrorMessages.EMAIL_ALREADY_EXISTS); 
        }
        
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.PLAYER)
                .build();
        
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        var user = repository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.EMAIL_NOT_FOUND));
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}
