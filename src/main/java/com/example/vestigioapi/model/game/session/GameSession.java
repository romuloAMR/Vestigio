package com.example.vestigioapi.model.game.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.model._common.Auditable;
import com.example.vestigioapi.model.game.move.Move;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "game_sessions")
public class GameSession extends Auditable {

    @Column(unique = true, nullable = false)
    private String roomCode;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne(optional = false)
    private Story story;

    @ManyToOne(optional = false)
    private User master;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "game_session_players")
    @Builder.Default
    private Set<User> players = new HashSet<>();

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Move> moves = new ArrayList<>();

    @Transient
    @Builder.Default
    private List<Story> storyOptions = new ArrayList<>();
}
