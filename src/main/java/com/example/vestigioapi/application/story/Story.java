package com.example.vestigioapi.application.story;

import com.example.vestigioapi.application.story.constants.Difficulty;
import com.example.vestigioapi.application.story.constants.Genre;
import com.example.vestigioapi.application.story.constants.StoryStatus;
import com.example.vestigioapi.framework.common.model.Auditable;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "stories")
public class Story extends Auditable {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enigmaticSituation;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fullSolution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
}
