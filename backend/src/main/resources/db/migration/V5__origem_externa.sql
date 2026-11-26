-- Mídias importadas de outras plataformas (ex.: UIRA). Mídias cadastradas aqui ficam com origem NULL.
ALTER TABLE midia ADD COLUMN origem VARCHAR(30);
ALTER TABLE midia ADD COLUMN id_externo VARCHAR(100);
ALTER TABLE midia ADD CONSTRAINT uk_midia_origem_id_externo UNIQUE (origem, id_externo);

-- Hash dos dados de origem: o importador só regrava a mídia quando ele muda.
ALTER TABLE midia ADD COLUMN hash_origem VARCHAR(64);
ALTER TABLE midia ADD COLUMN sincronizado_em TIMESTAMP(6) WITH TIME ZONE;

-- Dados extras do catálogo
ALTER TABLE midia ADD COLUMN classificacao_indicativa VARCHAR(10);
ALTER TABLE midia ADD COLUMN uf VARCHAR(10);
ALTER TABLE midia ADD COLUMN tags TEXT;
ALTER TABLE midia ADD COLUMN ficha_tecnica JSONB;

-- Sinopses e URLs de origem passam de 255 caracteres
ALTER TABLE midia ALTER COLUMN sinopse TYPE TEXT;
ALTER TABLE midia ALTER COLUMN url_poster TYPE VARCHAR(2048);
ALTER TABLE conteudo_assistivel ALTER COLUMN url TYPE VARCHAR(2048);
