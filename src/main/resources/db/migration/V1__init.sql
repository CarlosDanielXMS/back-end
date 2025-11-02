BEGIN;

CREATE TABLE public.clientes (
    id              INT         GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome            VARCHAR(50) NOT NULL,
    email           VARCHAR(50) NOT NULL UNIQUE,
    telefone        VARCHAR(15) NOT NULL,
    cpf             VARCHAR(11) NOT NULL UNIQUE,
    data_criacao    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE public.locacoes (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome            VARCHAR(50)     NOT NULL,
    tipo            VARCHAR(20)     NOT NULL,
    descricao       VARCHAR(255),
    valor_hora      NUMERIC(10, 2)  NOT NULL,
    tempo_minimo    INT             NOT NULL,
    tempo_maximo    INT             NOT NULL,
    data_criacao    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_locacoes_tempo CHECK (
        tempo_minimo > 0                AND
        tempo_maximo > 0                AND
        tempo_maximo >= tempo_minimo    AND
        valor_hora > 0
    ),
    CONSTRAINT ck_locacoes_tipo_enum CHECK (tipo IN ('RESIDENCIAL', 'NAO_RESIDENCIAL', 'TEMPORADA'))
);

CREATE TABLE public.reservas (
    id              INT             GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id      INT             NOT NULL,
    locacao_id      INT             NOT NULL,
    data_inicio     DATE            NOT NULL,
    data_fim        DATE            NOT NULL,
    valor_final     NUMERIC(10, 2)  NOT NULL,
    situacao        VARCHAR(20)     NOT NULL,
    data_criacao    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_reservas_situacao_enum CHECK (situacao IN ('PENDENTE','CONFIRMADA','CANCELADA','CONCLUIDA')),
    CONSTRAINT ck_reservas_periodo CHECK (data_fim > data_inicio),
    CONSTRAINT fk_reservas_cliente FOREIGN KEY (cliente_id) REFERENCES public.clientes(id),
    CONSTRAINT fk_reservas_locacao FOREIGN KEY (locacao_id) REFERENCES public.locacoes(id)
);

COMMIT;