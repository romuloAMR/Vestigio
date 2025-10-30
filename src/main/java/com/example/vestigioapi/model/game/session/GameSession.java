package com.example.vestigioapi.model.game.session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.vestigioapi.model._common.Auditable;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.user.User;
import jakarta.persistence.*;

@Entity
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
        name = "game_session_story_options",
        joinColumns = @JoinColumn(name = "game_session_id"),
        inverseJoinColumns = @JoinColumn(name = "story_id")
    )
    private List<Story> storyOptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

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
    private List<com.example.vestigioapi.model.game.move.Move> moves = new ArrayList<>();

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public User getMaster() {
        return master;
    }

    public void setMaster(User master) {
        this.master = master;
    }

    public List<Story> getStoryOptions() {
        return storyOptions;
    }

    public void setStoryOptions(List<Story> storyOptions) {
        this.storyOptions = storyOptions;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public List<com.example.vestigioapi.model.game.move.Move> getMoves() {
        return moves;
    }

    public void setMoves(List<com.example.vestigioapi.model.game.move.Move> moves) {
        this.moves = moves;
    }
}
