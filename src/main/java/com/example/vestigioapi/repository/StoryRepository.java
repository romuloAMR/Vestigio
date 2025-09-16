package com.example.vestigioapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.model.game.story.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {}
