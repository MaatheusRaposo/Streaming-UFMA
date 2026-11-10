ALTER TABLE midia ADD COLUMN data_lancamento DATE;
UPDATE midia SET data_lancamento = make_date(ano_lancamento, 1, 1);
ALTER TABLE midia DROP COLUMN ano_lancamento;

ALTER TABLE midia ADD COLUMN destaque BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE midia ADD COLUMN visualizacoes BIGINT NOT NULL DEFAULT 0;

create table avaliacao (nota integer not null, avaliado_em timestamp(6) with time zone, midia_id uuid not null, user_id uuid not null, primary key (midia_id, user_id));
create table item_lista (adicionado_em timestamp(6) with time zone, midia_id uuid not null, user_id uuid not null, tipo varchar(20) not null check ((tipo in ('MINHA_LISTA','FAVORITOS'))), primary key (midia_id, user_id, tipo));
create table sessao_reproducao (encerrada_em timestamp(6) with time zone, iniciada_em timestamp(6) with time zone, ultima_atividade timestamp(6) with time zone, conteudo_id uuid not null, sessao_id uuid not null, user_id uuid not null, primary key (sessao_id));
ALTER TABLE avaliacao
    ADD CONSTRAINT fk_avaliacao_midia FOREIGN KEY (midia_id) REFERENCES midia (midia_id);
ALTER TABLE avaliacao
    ADD CONSTRAINT fk_avaliacao_usuario FOREIGN KEY (user_id) REFERENCES usuario (user_id);
ALTER TABLE item_lista
    ADD CONSTRAINT fk_item_lista_midia FOREIGN KEY (midia_id) REFERENCES midia (midia_id);
ALTER TABLE item_lista
    ADD CONSTRAINT fk_item_lista_usuario FOREIGN KEY (user_id) REFERENCES usuario (user_id);
ALTER TABLE sessao_reproducao
    ADD CONSTRAINT fk_sessao_reproducao_conteudo FOREIGN KEY (conteudo_id) REFERENCES conteudo_assistivel (midia_id);
ALTER TABLE sessao_reproducao
    ADD CONSTRAINT fk_sessao_reproducao_usuario FOREIGN KEY (user_id) REFERENCES usuario (user_id);
