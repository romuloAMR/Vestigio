package com.example.vestigioapi.service.game.move;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.move.MoveResponseDTO;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.model.game.move.AnswerType;
import com.example.vestigioapi.model.game.move.Move;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.game.session.GameStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.GameSessionRepository;
import com.example.vestigioapi.repository.MoveRepository;
import com.example.vestigioapi.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoveService {
    private final MoveRepository moveRepository;
    private final GameSessionRepository gameSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public MoveResponseDTO processQuestion(String roomCode, Long detectiveId, String questionText) {
        GameSession gameSession = gameSessionRepository.findByRoomCode(roomCode).orElseThrow(() -> new IllegalArgumentException("Invalid game session room code"));
        User detective = userRepository.findById(detectiveId).orElseThrow(() -> new IllegalArgumentException("Invalid detective ID"));

        if (gameSession.getStatus() != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game session is not in progress.");
        }

        Move newMove = new Move();
        newMove.setGameSession(gameSession);
        newMove.setAuthor(detective);
        newMove.setQuestion(questionText);

        return toMoveResponseDTO(moveRepository.save(newMove));
    }

    @Transactional
    public MoveResponseDTO processAnswer(Long moveId, Long masterId, AnswerType answer) {
        Move move = moveRepository.findById(moveId).orElseThrow(() -> new IllegalArgumentException("Invalid move ID"));

        if (!move.getGameSession().getMaster().getId().equals(masterId)) {
            throw new SecurityException("Only the master can answer.");
        }

        move.setAnswer(answer);
        return toMoveResponseDTO(moveRepository.save(move));
    }

    private MoveResponseDTO toMoveResponseDTO(Move move) {
    return new MoveResponseDTO(
        move.getId(),
        move.getQuestion(),
        move.getAnswer(),
        new PlayerDTO(move.getAuthor().getId(), move.getAuthor().getUsername()),
        move.getCreatedAt()
    );
}

}

