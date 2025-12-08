package com.example.vestigioapi.application.trivia.game.move;

import com.example.vestigioapi.framework.engine.Move;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@PrimaryKeyJoinColumn(name = "moveId")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriviaMove extends Move {

    private Long questionId;

    private Integer selectedAnswerIndex;

    private Boolean isCorrect;

    @Builder.Default
    private Integer pointsEarned = 0;
}
