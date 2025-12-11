package com.example.vestigioapi.application.trivia.game;

import com.example.vestigioapi.application.trivia.game.move.TriviaMove;
import com.example.vestigioapi.framework.engine.GameSession;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trivia_game_sessions")
public class TriviaGameSession extends GameSession {

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id")
    private List<TriviaMove> triviaMove = new ArrayList<>();

    @Builder.Default
    private Integer totalQuestions = 0;

    @Builder.Default
    private Integer currentQuestionIndex = 0;

    private Long currentQuestionId;

    @Builder.Default
    private Integer playerScore = 0;
}
