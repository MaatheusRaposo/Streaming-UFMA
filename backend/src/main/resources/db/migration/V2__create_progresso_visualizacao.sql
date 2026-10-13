CREATE TABLE midia (
    midia_id UUID PRIMARY KEY,
    titulo VARCHAR(255),
    sinopse VARCHAR(255),
    ano_lancamento INT NOT NULL,
    url_poster VARCHAR(255),
    status VARCHAR(20) NOT NULL
);

CREATE TABLE midia_generos (
    midia_midia_id UUID NOT NULL,
    generos VARCHAR(255),
    CONSTRAINT fk_midia_generos_midia
        FOREIGN KEY (midia_midia_id) REFERENCES midia (midia_id)
);

CREATE TABLE conteudo_assistivel (
    midia_id UUID PRIMARY KEY,
    duracao_segundos INT NOT NULL,
    url VARCHAR(255),
    CONSTRAINT fk_conteudo_assistivel_midia
        FOREIGN KEY (midia_id) REFERENCES midia (midia_id)
);

CREATE TABLE filme (
    midia_id UUID PRIMARY KEY,
    CONSTRAINT fk_filme_conteudo_assistivel
        FOREIGN KEY (midia_id) REFERENCES conteudo_assistivel (midia_id)
);

CREATE TABLE serie (
    midia_id UUID PRIMARY KEY,
    total_temporadas INT NOT NULL,
    CONSTRAINT fk_serie_midia
        FOREIGN KEY (midia_id) REFERENCES midia (midia_id)
);

CREATE TABLE episodio (
    midia_id UUID PRIMARY KEY,
    temporada INT NOT NULL,
    numero INT NOT NULL,
    serie_id UUID NOT NULL,
    CONSTRAINT fk_episodio_conteudo_assistivel
        FOREIGN KEY (midia_id) REFERENCES conteudo_assistivel (midia_id),
    CONSTRAINT fk_episodio_serie
        FOREIGN KEY (serie_id) REFERENCES serie (midia_id)
);
