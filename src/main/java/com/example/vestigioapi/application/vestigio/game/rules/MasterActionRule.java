package com.example.vestigioapi.application.vestigio.game.rules;

import com.example.vestigioapi.application.vestigio.game.VestigioGameEngine;
import com.example.vestigioapi.application.vestigio.game.VestigioGameSession;
import com.example.vestigioapi.application.vestigio.util.VestigioErrorMessages;
import com.example.vestigioapi.framework.common.exception.ForbiddenActionException;
import com.example.vestigioapi.framework.engine.GameRule;
import com.example.vestigioapi.framework.engine.GameSession;
import com.example.vestigioapi.framework.user.model.User;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MasterActionRule implements GameRule {

    @Override
    public boolean supports(GameSession session) {
        return session instanceof VestigioGameSession;
    }

    @Override
    public void validate(GameSession session, User player, String actionType, Map<String, Object> payload) {
        boolean isMaster = session.getMaster().getId().equals(player.getId());

        if (isMaster && VestigioGameEngine.ACTION_ASK.equals(actionType)) {
            throw new ForbiddenActionException(VestigioErrorMessages.FORBIDDEN_MASTER_ASK_QUESTION);
        }

        if (!isMaster && VestigioGameEngine.ACTION_ANSWER.equals(actionType)) {
            throw new ForbiddenActionException(VestigioErrorMessages.FORBIDDEN_MASTER_ONLY_ANSWER);
        }
    }
}