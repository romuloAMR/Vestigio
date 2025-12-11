package com.example.vestigioapi.application.trivia.question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vestigioapi.application.trivia.question.constants.Difficulty;
import com.example.vestigioapi.application.trivia.question.constants.TriviaCategory;
import com.example.vestigioapi.framework.user.model.User;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCreator(User creator);

    List<Question> findByCategory(TriviaCategory category);

    List<Question> findByDifficulty(Difficulty difficulty);

    List<Question> findByCategoryAndDifficulty(TriviaCategory category, Difficulty difficulty);

    @Query(value = "SELECT * FROM trivia_questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);

    @Query(value = "SELECT * FROM trivia_questions WHERE category = :category ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("category") String category, @Param("limit") int limit);
}
