package com.example.vestigioapi.application.hangman.game;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.vestigioapi.application.hangman.game.dto.HangmanGameContent;
import com.example.vestigioapi.application.hangman.game.dto.HangmanMoveRequest;
import com.example.vestigioapi.application.hangman.game.dto.HangmanMoveResponse;
import com.example.vestigioapi.application.hangman.game.move.HangmanMove;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.engine.GameEngine;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.engine.Move;
import com.example.vestigioapi.framework.session.dto.PlayerDTO;
import com.example.vestigioapi.framework.user.model.User;

import lombok.RequiredArgsConstructor;

@Service("HANGMAN")
@RequiredArgsConstructor
public class HangmanGameEngine implements GameEngine<HangmanGameSession, HangmanGameContent, HangmanMoveResponse> {

    private final ObjectMapper objectMapper;
    private static final String GUESS_LETTER = "GUESS_LETTER";

    @Override
    public String getGameType() {
        return "HANGMAN";
    }

    @Override
    public HangmanGameSession createSession() {
        return new HangmanGameSession();
    }

    @Override
    public boolean supports(GameSession session) {
        return session instanceof HangmanGameSession;
    }

    @Override
    public void onGameStart(HangmanGameSession session, Map<String, Object> configParams) {
        session.setSecretWord("VESTIGIO"); 
        if (configParams != null && configParams.containsKey("word")) {
            session.setSecretWord(configParams.get("word").toString().toUpperCase());
        }
    }

    @Override
    public Move processMove(HangmanGameSession session, User player, String actionType, Map<String, Object> payload) {
        if (!GUESS_LETTER.equalsIgnoreCase(actionType)) {
             throw new BusinessRuleException("Action not supported: " + actionType);
        }

        HangmanMoveRequest request = objectMapper.convertValue(payload, HangmanMoveRequest.class);
        Character letter = Character.toUpperCase(request.letter());

        if (session.getGuessedLetters().contains(String.valueOf(letter))) {
            throw new BusinessRuleException("Letter already guessed: " + letter);
        }
        
        String previousGuesses = session.getGuessedLetters();
        session.setGuessedLetters(previousGuesses.isEmpty() ? String.valueOf(letter) : previousGuesses + "," + letter);
        
        boolean isCorrect = session.getSecretWord().indexOf(letter) >= 0;
        if (!isCorrect) {
            session.setWrongAttempts(session.getWrongAttempts() + 1);
        }

        HangmanMove move = new HangmanMove();
        move.setGuessedLetter(letter);
        move.setCorrect(isCorrect);
        move.setAuthor(player);
        move.setCreatedAt(LocalDateTime.now());
        
        return move;
    }

    @Override
    public boolean checkWinCondition(HangmanGameSession session) {
        String word = session.getSecretWord();
        String guessed = session.getGuessedLetters();
        
        for (char c : word.toCharArray()) {
            if (guessed.indexOf(c) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onGameEnd(HangmanGameSession session) {
    }

    @Override
    public HangmanGameContent getGameContent(HangmanGameSession session, Long viewerId) {
        String word = session.getSecretWord();
        String guessed = session.getGuessedLetters();
        StringBuilder masked = new StringBuilder();
        
        for (char c : word.toCharArray()) {
            if (guessed.indexOf(c) >= 0) {
                masked.append(c);
            } else {
                masked.append("_");
            }
        }
        
        int remaining = session.getMaxAttempts() - session.getWrongAttempts();
        boolean isGameOver =  remaining <= 0 || checkWinCondition(session);
        boolean isVictory = checkWinCondition(session);
        
        if (isGameOver && !isVictory) {
            masked = new StringBuilder(word);
        }

        return new HangmanGameContent(
            masked.toString(),
            guessed,
            remaining,
            session.getMaxAttempts(),
            isGameOver,
            isVictory
        );
    }

    @Override
    public List<HangmanGameContent> getContentOptions(HangmanGameSession session) {
        return Collections.emptyList();
    }

    @Override
    public List<HangmanMoveResponse> getGameMoves(HangmanGameSession session) {
         if (session.getMoves() == null) return Collections.emptyList();

        return session.getMoves().stream()
            .map(m -> (HangmanMove) m)
            .sorted(Comparator.comparing(HangmanMove::getCreatedAt))
            .map(this::toMoveDTO)
            .collect(Collectors.toList());
    }

    private HangmanMoveResponse toMoveDTO(HangmanMove move) {
        return new HangmanMoveResponse(
            move.getId(),
            move.getGuessedLetter(),
            move.isCorrect(),
            new PlayerDTO(move.getAuthor().getId(), move.getAuthor().getUsername()),
            move.getCreatedAt()
        );
    }

}
