package com.example.vestigioapi.application.hangman.word;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.framework.user.model.User;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByCreator(User creator);
}
