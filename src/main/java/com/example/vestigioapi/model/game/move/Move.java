package com.example.vestigioapi.model.game.move;

import com.example.vestigioapi.model._common.Auditable;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "moves")
public class Move extends Auditable {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;
    
    @Enumerated(EnumType.STRING)
    private AnswerType answer;

    @ManyToOne(optional = false)
    private User author;

    @ManyToOne(optional = false)
    private GameSession gameSession;
}
