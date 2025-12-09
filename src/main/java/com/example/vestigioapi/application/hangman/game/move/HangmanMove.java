package com.example.vestigioapi.application.hangman.game.move;

import com.example.vestigioapi.framework.engine.Move;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "moveId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangmanMove extends Move {
    
    private Character guessedLetter;
    private boolean isCorrect;

}
