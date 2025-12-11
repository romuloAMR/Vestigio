package com.example.vestigioapi.application.trivia.question;

import com.example.vestigioapi.application.trivia.question.constants.Difficulty;
import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;
import com.example.vestigioapi.framework.common.model.Auditable;
import com.example.vestigioapi.framework.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "trivia_questions")
public class Question extends Auditable {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private Integer correctAnswerIndex;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TriviaCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;
}
