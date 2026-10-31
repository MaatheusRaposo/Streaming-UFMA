ALTER TABLE midia ADD COLUMN data_lancamento DATE;
UPDATE midia SET data_lancamento = make_date(ano_lancamento, 1, 1);
ALTER TABLE midia DROP COLUMN ano_lancamento;

ALTER TABLE midia ADD COLUMN destaque BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE midia ADD COLUMN visualizacoes BIGINT NOT NULL DEFAULT 0;

create table avaliacao (nota integer not null, avaliado_em timestamp(6) with time zone, midia_id uuid not null, user_id uuid not null, primary key (midia_id, user_id));
create table item_lista (adicionado_em timestamp(6) with time zone, midia_id uuid not null, user_id uuid not null, tipo varchar(20) not null check ((tipo in ('MINHA_LISTA','FAVORITOS'))), primary key (midia_id, user_id, tipo));
create table sessao_reproducao (encerrada_em timestamp(6) with time zone, iniciada_em timestamp(6) with time zone, ultima_atividade timestamp(6) with time zone, conteudo_id uuid not null, sessao_id uuid not null, user_id uuid not null, primary key (sessao_id));
alter table if exists avaliacao add constraint FKc2e72w3rfypyt542jkbq3qs49 foreign key (midia_id) references midia;
alter table if exists avaliacao add constraint FKo18ayw98x8u81sjeacacw9b7 foreign key (user_id) references usuario;
alter table if exists item_lista add constraint FK1kj6g4ohykrxv1li69cwh41b2 foreign key (midia_id) references midia;
alter table if exists item_lista add constraint FKgxfnowhd5yiyfd9qjl6stx88w foreign key (user_id) references usuario;
alter table if exists sessao_reproducao add constraint FK1oghfmvg1me6hbr2wjk70pfm1 foreign key (conteudo_id) references conteudo_assistivel;
alter table if exists sessao_reproducao add constraint FKhj2ogry5102sgpvq71g83bgkv foreign key (user_id) references usuario;