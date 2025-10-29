package com.example.vestigioapi.service.game.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.move.AnswerQuestionRequestDTO;
import com.example.vestigioapi.dto.game.move.AskQuestionRequestDTO;
import com.example.vestigioapi.dto.game.session.GameSessionCreateDTO;
import com.example.vestigioapi.dto.game.session.GameSessionResponseDTO;
import com.example.vestigioapi.dto.game.session.MoveDTO;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.dto.game.story.StoryResponseDTO;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.game.session.GameStatus;
import com.example.vestigioapi.model.game.story.Story;
import com.example.vestigioapi.model.game.story.StoryStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.model.game.move.Move;
import com.example.vestigioapi.repository.GameSessionRepository;
import com.example.vestigioapi.repository.StoryRepository;
import com.example.vestigioapi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository; // <--- injetar repo de user

    public GameSessionResponseDTO createGameSession(GameSessionCreateDTO dto, User master) {
        Story story = storyRepository.findById(dto.storyId())
                .orElseThrow(() -> new EntityNotFoundException("Story not found with id: " + dto.storyId()));

        // garantir que o master seja um User gerenciado (com id) vindo do DB
        User managedMaster = userRepository.findById(master.getId())
                .orElseThrow(() -> new EntityNotFoundException("User (master) not found with id: " + master.getId()));

        GameSession session = new GameSession();
        session.setMaster(managedMaster);
        session.setStory(story);
        session.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        session.setRoomCode(generateUniqueRoomCode());
        session.getPlayers().add(managedMaster); // utilizar user gerenciado

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    public GameSessionResponseDTO joinGameSession(String roomCode, User player) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("Cannot join a game that has already started or finished.");
        }

        session.getPlayers().add(player);
        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    public GameSessionResponseDTO getGameSessionByRoomCode(String roomCode) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Game session not found with room code: " + roomCode));
        return toResponseDTO(session);
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

        // Map story options (do not include fullSolution)
        List<StoryResponseDTO> storyOptionsDTO = session.getStoryOptions() == null ? new ArrayList<>() :
            session.getStoryOptions().stream()
                .map(s -> new StoryResponseDTO(
                    s.getId(),
                    s.getTitle(),
                    s.getEnigmaticSituation(),
                    null, // do not send solution in options
                    s.getGenre(),
                    s.getDifficulty(),
                    s.getCreator() != null ? s.getCreator().getUsername() : "System"
                ))
                .collect(Collectors.toList());

        return new GameSessionResponseDTO(
                session.getId(),
                session.getRoomCode(),
                session.getStatus(),
                storyDTO,
                masterDTO,
                playersDTO,
                movesDTO,
                storyOptionsDTO,
                session.getCreatedAt()
        );
    }

    @Transactional
    public GameSessionResponseDTO endGame(String roomCode, User user) {
        GameSession session = gameSessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new EntityNotFoundException("Sessão de jogo não encontrada com o código: " + roomCode));

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new SecurityException("Apenas o mestre pode encerrar o jogo.");
        }

        session.setStatus(GameStatus.COMPLETED);

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }

    @Transactional
    public GameSessionResponseDTO askQuestion(String roomCode, AskQuestionRequestDTO dto, User author) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (session.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("O jogo não está em andamento.");
        }
        if (session.getMaster().getId().equals(author.getId())) {
            throw new IllegalStateException("O mestre não pode fazer perguntas.");
        }
        if (session.getPlayers().stream().noneMatch(p -> p.getId().equals(author.getId()))) {
            throw new SecurityException("Este jogador não pertence à partida.");
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
            throw new SecurityException("Apenas o mestre pode responder perguntas.");
        }

        Move move = session.getMoves().stream()
            .filter(m -> m.getId().equals(dto.moveId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Pergunta (Move) não encontrada com o ID: " + dto.moveId()));

        if (move.getAnswer() != null) {
            throw new IllegalStateException("Esta pergunta já foi respondida.");
        }

        move.setAnswer(dto.answer());

        GameSession updatedSession = gameSessionRepository.save(session);
        return toResponseDTO(updatedSession);
    }

    private GameSession findSessionByCodeOrThrow(String roomCode) {
        return gameSessionRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new EntityNotFoundException("Sessão de jogo não encontrada com o código: " + roomCode));
    }

    @Transactional
    public GameSessionResponseDTO startStorySelection(String roomCode, User user) {
        GameSession session = findSessionByCodeOrThrow(roomCode);

        if (!session.getMaster().getId().equals(user.getId())) {
            throw new SecurityException("Apenas o mestre pode iniciar a seleção.");
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_PLAYERS) {
            throw new IllegalStateException("O jogo não está aguardando jogadores.");
        }

        if (session.getPlayers().size() < 2) {
            throw new IllegalStateException("É necessário pelo menos 2 jogadores.");
        }

        List<Story> allStories = storyRepository.findAll();

        if (allStories.size() < 3) {
            throw new IllegalStateException("Não há histórias suficientes disponíveis no sistema.");
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
            throw new SecurityException("Apenas o mestre pode selecionar a história.");
        }

        if (session.getStatus() != GameStatus.WAITING_FOR_STORY_SELECTION) {
            throw new IllegalStateException("O jogo não está aguardando a seleção da história.");
        }

        Story chosenStory = storyRepository.findById(storyId)
            .orElseThrow(() -> new EntityNotFoundException("História não encontrada."));

        session.setStory(chosenStory);
        session.setStatus(GameStatus.IN_PROGRESS);
        session.getStoryOptions().clear();

        GameSession savedSession = gameSessionRepository.save(session);
        return toResponseDTO(savedSession);
    }
}
