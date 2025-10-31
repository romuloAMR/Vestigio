package com.example.vestigioapi.service.game.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.vestigioapi.dto.game.move.AnswerQuestionRequestDTO;
import com.example.vestigioapi.dto.game.move.AskQuestionRequestDTO;
import com.example.vestigioapi.dto.game.session.GameEvent;
import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.dto.game.session.MoveDTO;
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
import com.example.vestigioapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional
    public GameSessionResponseDTO pickWinner(String roomCode, Long winnerId, User master) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_NOT_FOUND));

        if (!session.getMaster().getId().equals(master.getId())) {
            throw new BusinessRuleException(ErrorMessages.FORBIDDEN_MASTER_ONLY_DEF_WINNER);
        }

        User winner = userRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.WINNER_NOT_FOUND));

        boolean winnerIsPlayer = session.getPlayers().stream()
                .anyMatch(p -> p.getId().equals(winner.getId()));

        if (!winnerIsPlayer) {
            throw new ResourceNotFoundException(ErrorMessages.FORBIDDEN_PLAYER_NOT_IN_SESSION);
        }

        session.setWinner(winner);
        session.setStatus(GameStatus.COMPLETED);
        GameSession saved = gameSessionRepository.save(session);

        return toResponseDTO(saved);
    }

    public GameSessionResponseDTO createGameSession(GameSessionCreateDTO dto, User master) {
        Story story = storyRepository.findById(dto.storyId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND + " com id: " + dto.storyId()));

        User managedMaster = userRepository.findById(master.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MASTER_NOT_FOUND + " com id: " + master.getId()));

        GameSession session = new GameSession();
        session.setMaster(managedMaster);
        session.setStory(story);
        session.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        session.setRoomCode(generateUniqueRoomCode());
        session.getPlayers().add(managedMaster);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Transactional
    public GameSessionResponseDTO joinGameSession(String roomCode, User player) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_INVALID_JOIN);
        }

        User managedPlayer = userRepository.findById(player.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if (session.getPlayers().stream().noneMatch(p -> p.getId().equals(managedPlayer.getId()))) {
            session.getPlayers().add(managedPlayer);
        }

        if (session.getStory() != null && session.getPlayers().size() >= 2) {
            session.setStatus(GameStatus.IN_PROGRESS);
        }

        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    @Transactional(readOnly = true)
    public GameSessionResponseDTO getGameSessionByRoomCode(String roomCode) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_SESSION_NOT_FOUND + " com código: " + roomCode));
        return toResponseDTO(session);
    }

    @Transactional
    public GameSessionResponseDTO startGameSession(String roomCode, Long userId) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_NOT_FOUND));

        if (!session.getMaster().getId().equals(userId)) {
            throw new BusinessRuleException(ErrorMessages.FORBIDDEN_MASTER_ONLY_START);
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_WAITING_PLAYERS);
        }

        session.setStatus(GameStatus.IN_PROGRESS);

        GameSession startedSession = gameSessionRepository.save(session);

        GameEvent<GameSession> event = new GameEvent<>("GAME_STARTED", startedSession);
        messagingTemplate.convertAndSend("/topic/session/" + roomCode, event);

        return toResponseDTO(startedSession);
    }

    @Transactional
    public GameSessionResponseDTO finishGameSession(String roomCode, Long usuarioRequisitanteId) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_NOT_FOUND));

        if (!session.getMaster().getId().equals(usuarioRequisitanteId)) {
            throw new BusinessRuleException(ErrorMessages.FORBIDDEN_MASTER_ONLY_END);
        }

        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_IN_PROGRESS);
        }

        session.setStatus(GameStatus.COMPLETED);

        GameSession finishedSession = gameSessionRepository.save(session);

        GameEvent<GameSessionResponseDTO> event = new GameEvent<>("GAME_FINISHED", toResponseDTO(finishedSession));
        messagingTemplate.convertAndSend("/topic/session/" + roomCode, event);

        return toResponseDTO(finishedSession);
    }

    private String generateUniqueRoomCode() {
        return RandomStringUtils.randomAlphanumeric(6).toUpperCase();
    }

    private GameSessionResponseDTO toResponseDTO(GameSession session) {
        StoryResponseDTO storyDTO = session.getStory() != null ? new StoryResponseDTO(
                session.getStory().getId(),
                session.getStory().getTitle(),
                session.getStory().getEnigmaticSituation(),
                session.getStory().getFullSolution(),
                session.getStory().getGenre(),
                session.getStory().getDifficulty(),
                session.getStory().getCreator() != null ?
                    session.getStory().getCreator().getUsername() : "System"
        ) : null;

        PlayerDTO masterDTO = new PlayerDTO(
            session.getMaster().getId(),
            session.getMaster().getUsername()
        );

        Set<PlayerDTO> playersDTO = session.getPlayers().stream()
                .map(player -> new PlayerDTO(player.getId(), player.getUsername()))
                .collect(Collectors.toSet());

        List<MoveDTO> movesDTO = session.getMoves() == null ? new ArrayList<>() :
            session.getMoves().stream()
                .sorted(Comparator.comparing(Move::getCreatedAt))
                .map(move -> new MoveDTO(
                    move.getId(),
                    move.getQuestion(),
                    move.getAnswer(),
                    move.getAuthor() != null ? move.getAuthor().getUsername() : null,
                    move.getCreatedAt()
                ))
                .collect(Collectors.toList());

        List<StoryResponseDTO> storyOptionsDTO = session.getStoryOptions() == null ? new ArrayList<>() :
            session.getStoryOptions().stream()
                .map(s -> new StoryResponseDTO(
                    s.getId(),
                    s.getTitle(),
                    s.getEnigmaticSituation(),
                    null,
                    s.getGenre(),
                    s.getDifficulty(),
                    s.getCreator() != null ? s.getCreator().getUsername() : "System"
                ))
                .collect(Collectors.toList());

        PlayerDTO winnerDTO = session.getWinner() != null
            ? new PlayerDTO(session.getWinner().getId(), session.getWinner().getUsername())
            : null;

        return new GameSessionResponseDTO(
                session.getId(),
                session.getRoomCode(),
                session.getStatus(),
                storyDTO,
                masterDTO,
                playersDTO,
                movesDTO,
                storyOptionsDTO,
                winnerDTO,
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
    public GameSessionResponseDTO startStorySelection(String roomCode, User user) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_MASTER_ONLY_START);
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_WAITING_PLAYERS);
        }

        if (session.getPlayers().size() < 2) {
            throw new BusinessRuleException(ErrorMessages.GAME_REQUIRES_MIN_PLAYERS);
        }

        List<Story> allStories = storyRepository.findAll();

        if (allStories.size() < 3) {
            throw new BusinessRuleException(ErrorMessages.INSUFFICIENT_STORIES);
        }

        Collections.shuffle(allStories);
        List<Story> storyOptions = allStories.stream().limit(3).collect(Collectors.toList());

        session.setStoryOptions(storyOptions);
        session.setStatus(GameStatus.WAITING_FOR_STORY_SELECTION);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Transactional
    public GameSessionResponseDTO selectStoryAndStartGame(String roomCode, Long storyId, User user) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new BusinessRuleException(ErrorMessages.ONLY_MASTER_CAN_SELECT_STORY);
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_STORY_SELECTION) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_WAITING_FOR_STORY);
        }

        Story chosenStory = storyRepository.findById(storyId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STORY_NOT_FOUND));

        session.setStory(chosenStory);
        session.setStatus(GameStatus.IN_PROGRESS);
        session.getStoryOptions().clear();

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }
}
