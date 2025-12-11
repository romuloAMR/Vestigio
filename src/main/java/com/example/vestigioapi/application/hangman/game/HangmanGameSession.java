package com.example.vestigioapi.application.hangman.game;

import com.example.vestigioapi.framework.engine.GameSession;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "gameSessionId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangmanGameSession extends GameSession {
    
    private String secretWord;
    private String guessedLetters = "";
    private int maxAttempts = 6;
    private int wrongAttempts = 0;

}
