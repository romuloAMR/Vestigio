package com.example.vestigioapi.core.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.core.common.model.Auditable;
import com.example.vestigioapi.core.session.model.GameStatus;
import com.example.vestigioapi.core.user.model.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "game_sessions")
public class GameSession extends Auditable {

    @Column(unique = true, nullable = false)
    private String roomCode;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id")
    private User master;

    @ManyToMany
    @JoinTable(
        name = "game_session_players",
        joinColumns = @JoinColumn(name = "game_session_id"),
        inverseJoinColumns = @JoinColumn(name = "players_id")
    )
    private Set<User> players = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private User winner;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Move> moves = new ArrayList<>();

}
