package com.example.vestigioapi.framework.common.util;

public class ErrorMessages {
    public static final String INTERNAL_SEVER_ERROR = "Ocorreu um erro interno. Tente novamente mais tarde.";
    public static final String DATA_CONFLICT = "Conflito de dados.";
    
    public static final String INVALID_INPUT = "Dados de entrada inválidos.";
    
    public static final String TOKEN_EXPIRED = "Token expirado.";
    public static final String INVALID_TOKEN = "Token inválido.";

    public static final String USER_NOT_FOUND = "Usuário não encontrado.";
    public static final String USER_UNAUTHORISED = "Usuário não autenticado.";

    public static final String EMAIL_NOT_FOUND = "Email não encontrado.";
    public static final String EMAIL_ALREADY_EXISTS = "Email já está em uso.";

    public static final String INCORRECT_PASSWORD = "A senha está incorreta.";
    public static final String NEW_PASSWORD_MISMATCH = "A nova senha e a confirmação não coincidem.";
    public static final String NEW_PASSWORD_IS_SAME = "A nova senha deve ser diferente da senha atual.";
    public static final String DELETE_PASSWORD_INCORRECT = "A senha fornecida está incorreta. A exclusão foi cancelada.";

    public static final String STORY_NOT_FOUND = "Estoria não encontrada";
    public static final String STORY_REJECTED = "O texto fere as diretrizes de uso (direitos humanos)";
    public static final String INSUFFICIENT_STORIES = "Não há histórias suficientes disponíveis no sistema.";
    public static final String UNAUTHORIZED_ACTION_IN_STORY = "Você não tem permissão para modificar a estória.";
    
    public static final String GAME_SESSION_NOT_FOUND = "Sessão de jogo não encontrada";

    public static final String MOVE_NOT_FOUND = "Pergunta não encontrada";
    public static final String MOVE_ALREADY_ANSWERED = "Esta pergunta já foi respondida.";
    public static final String INVALID_MOVE_ID = "Jogada com id inválido";


    public static final String GAME_STATUS_INVALID_JOIN = "Não é possível entrar em um jogo que já começou ou terminou.";
    public static final String GAME_STATUS_NOT_IN_PROGRESS = "O jogo não está em andamento.";
    public static final String GAME_STATUS_NOT_WAITING_FOR_STORY = "O jogo não está aguardando a seleção da história.";
    public static final String GAME_STATUS_NOT_WAITING_PLAYERS = "O jogo não está aguardando jogadores.";
    public static final String GAME_REQUIRES_MIN_PLAYERS = "É necessário pelo menos 2 jogadores.";
    public static final String GAME_NOT_FOUND = "Sala não encontrada";

    public static final String FORBIDDEN_MASTER_ONLY_END = "Apenas o mestre pode encerrar o jogo.";
    public static final String FORBIDDEN_MASTER_ONLY_ANSWER = "Apenas o mestre pode responder perguntas.";
    public static final String FORBIDDEN_MASTER_ONLY_START = "Apenas o mestre pode iniciar o jogo.";
    public static final String FORBIDDEN_MASTER_ONLY_DEF_WINNER = "Apenas o mestre pode escolher o vencedor";
    public static final String FORBIDDEN_PLAYER_NOT_IN_SESSION = "Este jogador não pertence à partida.";
    public static final String FORBIDDEN_MASTER_ASK_QUESTION = "O mestre não pode fazer perguntas.";
    public static final String MASTER_NOT_FOUND = "O mestre não foi encontrado.";
    public static final String ONLY_MASTER_CAN_SELECT_STORY = "Apenas o mestre pode selecionar a história.";
    public static final String DETECTIVE_NOT_FOUND = "Detetive não encontrado";
    public static final String WINNER_NOT_FOUND = "Campeão não encontrado";

    public static final String ENGINE_NOT_FOUND = "Engine não configurada para este tipo de sessão.";
}
