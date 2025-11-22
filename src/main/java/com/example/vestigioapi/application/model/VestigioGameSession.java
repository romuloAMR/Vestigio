package com.example.vestigioapi.application.model;

import java.util.List;

import com.example.vestigioapi.framework.engine.GameSession;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
public class VestigioGameSession extends GameSession {
    
    @ManyToOne
    private Story currentStory;

    @ManyToMany
    private List<Story> storyOptions;

}
