package com.example.vestigioapi.service.game.move;

import org.springframework.stereotype.Service;

import com.example.vestigioapi.dto.game.move.MoveResponseDTO;
import com.example.vestigioapi.dto.game.session.PlayerDTO;
import com.example.vestigioapi.exception.BusinessRuleException;
import com.example.vestigioapi.exception.ForbiddenActionException;
import com.example.vestigioapi.exception.ResourceNotFoundException;
import com.example.vestigioapi.model.game.move.AnswerType;
import com.example.vestigioapi.model.game.move.Move;
import com.example.vestigioapi.model.game.session.GameSession;
import com.example.vestigioapi.model.game.session.GameStatus;
import com.example.vestigioapi.model.user.User;
import com.example.vestigioapi.repository.GameSessionRepository;
import com.example.vestigioapi.repository.MoveRepository;
import com.example.vestigioapi.repository.UserRepository;
import com.example.vestigioapi.util.ErrorMessages;

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
        GameSession gameSession = gameSessionRepository.findByRoomCode(roomCode)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.GAME_NOT_FOUND));
        User detective = userRepository.findById(detectiveId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.DETECTIVE_NOT_FOUND));

        if (gameSession.getStatus() != GameStatus.IN_PROGRESS) {
            throw new BusinessRuleException(ErrorMessages.GAME_STATUS_NOT_IN_PROGRESS);
        }

        Move newMove = new Move();
        newMove.setGameSession(gameSession);
        newMove.setAuthor(detective);
        newMove.setQuestion(questionText);

        return toMoveResponseDTO(moveRepository.save(newMove));
    }

    @Transactional
    public MoveResponseDTO processAnswer(Long moveId, Long masterId, AnswerType answer) {
        Move move = moveRepository.findById(moveId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.INVALID_MOVE_ID));

        if (!move.getGameSession().getMaster().getId().equals(masterId)) {
            throw new ForbiddenActionException(ErrorMessages.FORBIDDEN_MASTER_ONLY_ANSWER);
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

