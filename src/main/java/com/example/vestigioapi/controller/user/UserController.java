package com.example.vestigioapi.controller.user;

import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.UserRepository;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repository;

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        final List<User> users = repository.findAll();
        return ResponseEntity.ok(users);
    }
    
}
