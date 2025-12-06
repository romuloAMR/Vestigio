package com.example.vestigioapi.application.game.move;

import com.example.vestigioapi.framework.engine.Move;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class VestigioMove extends Move {
    @Column(columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    private AnswerType answer;
}
