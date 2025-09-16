package com.example.vestigioapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.model.game.session.GameSession;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
     Optional<GameSession> findByRoomCode(String roomCode);
}
