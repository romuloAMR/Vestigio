package com.example.vestigioapi.core.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.core.engine.Move;

public interface MoveRepository extends JpaRepository<Move, Long> {}
