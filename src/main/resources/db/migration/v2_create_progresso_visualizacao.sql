CREATE TABLE progresso_visualizacao (
    id UUID PRIMARY KEY,
    ep_id UUID,
    user_id UUID FOREIGN KEY,
    tempo_assistido_segundos INT,
    ultima_visualizacao DATE,
    concluido BOOLEAN
);
