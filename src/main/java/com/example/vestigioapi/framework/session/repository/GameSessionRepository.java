package com.example.vestigioapi.framework.session.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.framework.engine.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
     Optional<GameSession> findByRoomCode(String roomCode);
}
