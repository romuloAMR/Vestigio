package com.example.vestigioapi.core.engine;

import com.example.vestigioapi.core.common.model.Auditable;
import com.example.vestigioapi.core.user.model.User;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "moves")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Move extends Auditable {
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    @ManyToOne
    @JoinColumn(name = "game_session_id")
    private GameSession gameSession;

}
