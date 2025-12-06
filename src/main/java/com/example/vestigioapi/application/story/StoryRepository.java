package com.example.vestigioapi.application.story;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.framework.user.model.User;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByCreator(User creator);
}
