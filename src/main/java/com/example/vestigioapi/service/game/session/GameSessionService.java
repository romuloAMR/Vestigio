package com.example.vestigioapi.service.game.session;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.move.AnswerQuestionRequestDTO;
import com.example.vestigioapi.dto.game.move.AskQuestionRequestDTO;
import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.exception.BusinessRuleException;
import com.example.vestigioapi.exception.ForbiddenActionException;
import com.example.vestigioapi.exception.ResourceNotFoundException;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.game.session.GameStatus;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.model.game.move.Move;
import com.example.vestigioapi.repository.GameSessionRepository;
import com.example.vestigioapi.repository.StoryRepository;
import com.example.vestigioapi.util.ErrorMessages;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final StoryRepository storyRepository;

    public GameSessionResponseDTO createGameSession(GameSessionCreateDTO dto, User master) {
        Story story = storyRepository.findById(dto.storyId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND + " com id: " + dto.storyId()));

        GameSession session = new GameSession();
        session.setMaster(master);
        session.setStory(story);
        session.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        session.setRoomCode(generateUniqueRoomCode());
        session.getPlayers().add(master);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    public GameSessionResponseDTO joinGameSession(String roomCode, User player) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_INVALID_JOIN);
        }

        session.getPlayers().add(player);
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    public GameSessionResponseDTO getGameSessionByRoomCode(String roomCode) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));
        return toResponseDTO(session);
    }

    private String generateUniqueRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private GameSessionResponseDTO toResponseDTO(GameSession session) {
        StoryResponseDTO storyDTO = new StoryResponseDTO(
                session.getStory().getId(),
                session.getStory().getTitle(),
                session.getStory().getEnigmaticSituation(),
                session.getStory().getFullSolution(),
                session.getStory().getGenre(),
                session.getStory().getDifficulty(),
                session.getStory().getCreator().getUsername()
        );

        PlayerDTO masterDTO = new PlayerDTO(session.getMaster().getId(), session.getMaster().getUsername());

        return new GameSessionResponseDTO(
                session.getId(),
                session.getRoomCode(),
                session.getStatus(),
                storyDTO,
                masterDTO,
                session.getPlayers().stream()
                        .map(player -> new PlayerDTO(player.getId(), player.getUsername()))
                        .collect(Collectors.toSet()),
                session.getCreatedAt()
        );
    }

    @Transactional
    public GameSessionResponseDTO endGame(String roomCode, User user) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_MASTER_ONLY_END);
        }

        session.setStatus(GameStatus.COMPLETED);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Transactional
    public GameSessionResponseDTO askQuestion(String roomCode, AskQuestionRequestDTO dto, User author) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_IN_PROGRESS);
        }
        if (session.getMaster().getId().equals(author.getId())) {
            throw new BusinessRuleException(ErrorMessages.FORBIDDEN_MASTER_ASK_QUESTION);
        }
        if (session.getPlayers().stream().noneMatch(p -> p.getId().equals(author.getId()))) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_PLAYER_NOT_IN_SESSION);
        }

        Move newMove = new Move();
        newMove.setQuestion(dto.questionText());
        newMove.setAuthor(author);
        newMove.setGameSession(session);

        session.getMoves().add(newMove);
        
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    @Transactional
    public GameSessionResponseDTO answerQuestion(String roomCode, AnswerQuestionRequestDTO dto, User master) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (!session.getMaster().getId().equals(master.getId())) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_MASTER_ONLY_ANSWER);
        }

        Move move = session.getMoves().stream()
            .filter(m -> m.getId().equals(dto.moveId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVE_NOT_FOUND + " com o ID: " + dto.moveId()));
        
        if (move.getAnswer() != null) {
            throw new BusinessRuleException(ErrorMessages.MOVE_ALREADY_ANSWERED);
        }

        move.setAnswer(dto.answer());
        
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    private GameSession findSessionByCodeOrThrow(String roomCode) {
        return gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));
    }

    @Transactional
    public GameSessionResponseDTO selectStoryAndStartGame(String roomCode, Long storyId, User user) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_MASTER_ONLY_START);
        }
        if (session.getStatus() != GameStatus.WAITING_FOR_STORY_SELECTION) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_WAITING_FOR_STORY);
        }

        Story chosenStory = session.getStoryOptions().stream()
            .filter(s -> s.getId().equals(storyId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND));

        storyRepository.save(chosenStory);

        session.setStory(chosenStory);
        session.setStatus(GameStatus.IN_PROGRESS);
        session.setStoryOptions(new ArrayList<>());

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }
}
