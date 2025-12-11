package com.example.vestigioapi.framework.session.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.vestigioapi.framework.engine.Move;

public interface MoveRepository extends JpaRepository<Move, Long> {}
