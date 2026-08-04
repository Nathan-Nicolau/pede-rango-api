-- =============================================================================
-- Seed de Dados Iniciais — Food Delivery API
-- Senha padrão para todos os usuários: 123456
-- Hash BCrypt de '123456': $2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm
-- =============================================================================

-- ─── 1. USUÁRIOS ─────────────────────────────────────────────────────────────

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Administrador do Sistema',
       'admin@fooddelivery.com',
       '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm',
       'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Carlos Cliente',
       'cliente@fooddelivery.com',
       '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm',
       'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'cliente@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'João Entregador',
       'entregador@fooddelivery.com',
       '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm',
       'ENTREGADOR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'entregador@fooddelivery.com');

-- ─── 2. PERFIL CLIENTE ────────────────────────────────────────────────────────

INSERT INTO clientes (usuario_id, cpf, telefone, endereco_padrao, ativo, criado_em, atualizado_em)
SELECT u.id,
       '123.456.789-00',
       '(11) 91111-2222',
       'Rua das Flores, 123 — Centro, São Paulo/SP',
       TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.email = 'cliente@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM clientes c WHERE c.usuario_id = u.id);

-- ─── 3. PERFIL ENTREGADOR ─────────────────────────────────────────────────────

INSERT INTO entregadores (usuario_id, telefone, veiculo, placa, disponivel, ativo, criado_em, atualizado_em)
SELECT u.id,
       '(11) 98765-4321',
       'Moto Honda CG 160',
       'ABC-1234',
       TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.email = 'entregador@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM entregadores e WHERE e.usuario_id = u.id);

-- ─── 4. CARDÁPIO DE PRODUTOS ──────────────────────────────────────────────────

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'X-Burguer Especial',
       'Pão brioche tostado, hambúrguer artesanal 180g, queijo cheddar, alface, tomate e maionese da casa.',
       18.00, 'Lanches', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'X-Burguer Especial');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'X-Bacon Duplo',
       'Pão brioche, dois hambúrgueres 150g, bacon crocante, queijo prato e molho barbecue.',
       24.90, 'Lanches', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'X-Bacon Duplo');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Pizza Calabresa Grande',
       'Massa artesanal, molho de tomate, muçarela, calabresa fatiada, cebola e azeitonas.',
       49.90, 'Pizzas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Pizza Calabresa Grande');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Pizza Margherita',
       'Molho de tomate italiano, muçarela de búfala, tomate cereja e manjericão fresco.',
       47.00, 'Pizzas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Pizza Margherita');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Batata Frita Rústica',
       'Porção 300g de batata rústica com casca, temperada com ervas e acompanha molho cheddar.',
       18.50, 'Acompanhamentos', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Batata Frita Rústica');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Onion Rings',
       'Anéis de cebola empanados e fritos na hora, servidos com molho ranch.',
       14.00, 'Acompanhamentos', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Onion Rings');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Refrigerante Lata 350ml',
       'Escolha entre Coca-Cola, Guaraná Antarctica ou Sprite — bem gelado.',
       7.00, 'Bebidas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Refrigerante Lata 350ml');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Petit Gâteau de Chocolate',
       'Bolo quente de chocolate belga com centro cremoso, acompanha bola de sorvete de creme.',
       22.00, 'Sobremesas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Petit Gâteau de Chocolate');
