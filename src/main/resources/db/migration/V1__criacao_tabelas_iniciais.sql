-- V1__criacao_tabelas_iniciais.sql
-- Estrutura inicial consolidada de banco de dados do Pede Rango

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(50) NOT NULL DEFAULT 'CLIENTE' CHECK (perfil IN ('CLIENTE', 'ENTREGADOR', 'RESTAURANTE', 'ADMIN')),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    cpf VARCHAR(20),
    telefone VARCHAR(30),
    endereco_padrao VARCHAR(300),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS entregadores (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    telefone VARCHAR(50) NOT NULL,
    veiculo VARCHAR(100) NOT NULL,
    placa VARCHAR(20),
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(500),
    preco NUMERIC(10,2) NOT NULL,
    categoria VARCHAR(50),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    cliente_id BIGINT,
    nome_cliente VARCHAR(255) NOT NULL,
    endereco_entrega VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('RECEBIDO', 'EM_PREPARO', 'PRONTO_PARA_ENTREGA', 'SAIU_PARA_ENTREGA', 'ENTREGUE', 'CANCELADO')),
    forma_pagamento VARCHAR(50) NOT NULL DEFAULT 'DINHEIRO' CHECK (forma_pagamento IN ('DINHEIRO', 'PIX', 'CARTAO_CREDITO', 'CARTAO_DEBITO')),
    troco_para NUMERIC(10,2),
    taxa_entrega NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    entregador_id BIGINT,
    valor_total NUMERIC(38,2) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_entrega TIMESTAMP,
    atualizado_em TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL,
    FOREIGN KEY (entregador_id) REFERENCES entregadores(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT,
    nome VARCHAR(255) NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(38,2) NOT NULL,
    observacao VARCHAR(255),
    pedido_id BIGINT,
    FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE SET NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cancelamentos_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    motivo VARCHAR(100) NOT NULL CHECK (motivo IN ('DESISTENCIA_CLIENTE', 'ENDERECO_NAO_ENCONTRADO', 'FALTA_DE_ESTOQUE', 'ATRASO_NA_ENTREGA', 'OUTRO')),
    observacoes TEXT,
    cancelado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS configuracao_estabelecimento (
    id BIGSERIAL PRIMARY KEY,
    nome_loja VARCHAR(100) NOT NULL DEFAULT 'Pede Rango Delivery',
    telefone_contato VARCHAR(30) DEFAULT '(11) 98765-4321',
    chave_pix VARCHAR(100) DEFAULT 'financeiro@pederango.com',
    taxa_entrega_padrao NUMERIC(10,2) NOT NULL DEFAULT 5.00,
    tempo_estimado_min_minutos INTEGER NOT NULL DEFAULT 30,
    tempo_estimado_max_minutos INTEGER NOT NULL DEFAULT 45,
    loja_aberta BOOLEAN NOT NULL DEFAULT FALSE,
    data_expediente DATE,
    aberto_em TIMESTAMP,
    fechado_em TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Inserir configuração padrão inicial caso não exista
INSERT INTO configuracao_estabelecimento (id, nome_loja, telefone_contato, chave_pix, taxa_entrega_padrao, tempo_estimado_min_minutos, tempo_estimado_max_minutos, loja_aberta, atualizado_em)
SELECT 1, 'Pede Rango Delivery', '(11) 98765-4321', 'financeiro@pederango.com', 5.00, 30, 45, FALSE, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM configuracao_estabelecimento WHERE id = 1);
