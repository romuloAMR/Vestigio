package com.example.vestigioapi.application.game.rules;

import com.example.vestigioapi.application.game.VestigioGameEngine;
import com.example.vestigioapi.application.game.VestigioGameSession;
import com.example.vestigioapi.application.game.move.VestigioMove;
import com.example.vestigioapi.application.util.VestigioErrorMessages;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.engine.GameRule;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.user.model.User;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MoveAlreadyAnsweredRule implements GameRule {

    @Override
    public boolean supports(GameSession session) {
        return session instanceof VestigioGameSession;
    }

    @Override
    public void validate(GameSession session, User player, String actionType, Map<String, Object> payload) {
        
        if (!VestigioGameEngine.ACTION_ANSWER.equals(actionType)) {
            return;
        }

        if (!payload.containsKey("moveId")) {
            throw new BusinessRuleException("moveId é obrigatório");
        }
        
        Long moveId = Long.valueOf(payload.get("moveId").toString());

        VestigioMove targetMove = (VestigioMove) session.getMoves().stream()
            .filter(m -> m.getId().equals(moveId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.MOVE_NOT_FOUND));

        if (targetMove.getAnswer() != null) {
            throw new BusinessRuleException(VestigioErrorMessages.MOVE_ALREADY_ANSWERED);
        }
    }
}