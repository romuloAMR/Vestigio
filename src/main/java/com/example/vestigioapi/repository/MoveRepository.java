package com.example.vestigioapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.vestigioapi.model.game.move.Move;

public interface MoveRepository extends JpaRepository<Move, Long> {}
