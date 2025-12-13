package com.example.vestigioapi.application.vestigio.story;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.vestigioapi.framework.user.model.User;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByCreator(User creator);

    @Query(value = "SELECT * FROM story ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Story> findRandomStories(@Param("limit") int limit);
}
