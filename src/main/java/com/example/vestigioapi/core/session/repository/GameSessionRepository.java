package com.example.vestigioapi.core.session.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.core.session.model.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
     Optional<GameSession> findByRoomCode(String roomCode);
}
