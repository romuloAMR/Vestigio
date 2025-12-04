package com.example.vestigioapi.framework.common.util;

public class ErrorMessages {
    public static final String INTERNAL_SEVER_ERROR = "error.internal";
    public static final String DATA_CONFLICT = "error.data_conflict";
    
    public static final String INVALID_INPUT = "error.invalid_input";
    
    public static final String TOKEN_EXPIRED = "auth.token_expired";
    public static final String INVALID_TOKEN = "auth.invalid_token";

    public static final String USER_NOT_FOUND = "user.not_found";
    public static final String USER_UNAUTHORISED = "user.unauthorized";

    public static final String EMAIL_NOT_FOUND = "user.email_not_found";
    public static final String EMAIL_ALREADY_EXISTS = "user.email_already_exists";

    public static final String INCORRECT_PASSWORD = "user.incorrect_password";
    public static final String NEW_PASSWORD_MISMATCH = "user.new_password_mismatch";
    public static final String NEW_PASSWORD_IS_SAME = "user.new_password_same";
    public static final String DELETE_PASSWORD_INCORRECT = "user.delete_password_incorrect";


    public static final String GAME_SESSION_NOT_FOUND = "game.session_not_found";

    public static final String MOVE_NOT_FOUND = "move.not_found";

    public static final String INVALID_MOVE_ID = "move.invalid_id";


    public static final String GAME_STATUS_INVALID_JOIN = "game.status.invalid_join";
    public static final String GAME_STATUS_NOT_IN_PROGRESS = "game.status.not_in_progress";
    public static final String GAME_STATUS_NOT_WAITING_FOR_STORY = "game.status.not_waiting_story";
    public static final String GAME_STATUS_NOT_WAITING_PLAYERS = "game.status.not_waiting_players";
    public static final String GAME_REQUIRES_MIN_PLAYERS = "game.requires_min_players";
    public static final String GAME_NOT_FOUND = "game.not_found";

    public static final String FORBIDDEN_MASTER_ONLY_END = "game.forbidden.master_only_end";

    public static final String FORBIDDEN_MASTER_ONLY_START = "game.forbidden.master_only_start";
    public static final String FORBIDDEN_MASTER_ONLY_DEF_WINNER = "game.forbidden.master_only_def_winner";
    public static final String FORBIDDEN_PLAYER_NOT_IN_SESSION = "game.forbidden.player_not_in_session";

    public static final String MASTER_NOT_FOUND = "game.master_not_found";

    public static final String WINNER_NOT_FOUND = "game.winner_not_found";
    
    public static final String ENGINE_NOT_FOUND = "game.engine_not_found";

    public static final String UNKNOWN_ANSWER_TYPE = "error.unknown_answer_type";
    public static final String INVALID_GAME_TYPE_OR_ENGINE = "game.invalid_type_or_engine";

}
