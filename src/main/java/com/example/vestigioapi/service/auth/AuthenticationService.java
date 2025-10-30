package com.example.vestigioapi.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.auth.AuthenticationRequest;
import com.example.vestigioapi.dto.auth.AuthenticationResponse;
import com.example.vestigioapi.dto.auth.RegisterRequest;
import com.example.vestigioapi.exception.BusinessRuleException;
import com.example.vestigioapi.exception.ResourceNotFoundException;
import com.example.vestigioapi.model.user.Role;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;
import com.example.vestigioapi.util.ErrorMessages;

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
