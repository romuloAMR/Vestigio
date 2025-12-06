package com.example.vestigioapi.framework.engine;

import java.util.Map;
import com.example.vestigioapi.framework.user.model.User;

public interface GameRule {
    boolean supports(GameSession session);
    void validate(GameSession session, User player, String actionType, Map<String, Object> payload);
}