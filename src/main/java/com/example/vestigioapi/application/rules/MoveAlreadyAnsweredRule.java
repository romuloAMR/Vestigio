package com.example.vestigioapi.application.rules;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.vestigioapi.application.model.move.VestigioMove;
import com.example.vestigioapi.application.model.session.VestigioGameSession;
import com.example.vestigioapi.application.service.VestigioGameEngine;
import com.example.vestigioapi.application.util.VestigioErrorMessages;
import com.example.vestigioapi.framework.common.exception.BusinessRuleException;
import com.example.vestigioapi.framework.common.exception.ResourceNotFoundException;
import com.example.vestigioapi.framework.common.util.ErrorMessages;
import com.example.vestigioapi.framework.engine.GameRule;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.user.model.User;

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