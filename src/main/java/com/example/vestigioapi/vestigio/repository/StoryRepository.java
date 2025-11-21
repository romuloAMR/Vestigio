package com.example.vestigioapi.vestigio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.core.user.model.User;
import com.example.vestigioapi.vestigio.model.Story;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByCreator(User creator);
}
