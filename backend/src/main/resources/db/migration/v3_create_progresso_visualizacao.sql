CREATE TABLE progresso_visualizacao (
    conteudo_id UUID NOT NULL,
    user_id UUID NOT NULL,
    tempo_assistido_segundos INT NOT NULL,
    ultima_visualizacao TIMESTAMP,
    concluido BOOLEAN NOT NULL,
    CONSTRAINT pk_progresso_visualizacao
        PRIMARY KEY (conteudo_id, user_id),
    CONSTRAINT fk_progresso_conteudo
        FOREIGN KEY (conteudo_id) REFERENCES conteudo_assistivel (midia_id),
    CONSTRAINT fk_progresso_usuario
        FOREIGN KEY (user_id) REFERENCES usuario (user_id)
);
