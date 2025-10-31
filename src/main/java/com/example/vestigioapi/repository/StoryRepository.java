package com.example.vestigioapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.user.User;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByCreator(User creator);
}
