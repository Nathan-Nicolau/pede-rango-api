-- =============================================================================
--  Pede Rango API — Script de Primeiro Acesso
--  Gerado em: 2026-08-03
--
--  COMO USAR (com o Docker rodando):
--    psql -h localhost -p 5433 -U pede_rango_user -d pede_rango_db -f seed_inicial.sql
--
--  Ou dentro do container:
--    docker exec -i pede_rango_db psql -U pede_rango_user -d pede_rango_db < seed_inicial.sql
--
--  CREDENCIAIS DE ACESSO (senha padrão para todos: 123456)
--  ┌──────────────────────────────────┬──────────────────────────────┬─────────────┐
--  │ Perfil                           │ E-mail                       │ Senha       │
--  ├──────────────────────────────────┼──────────────────────────────┼─────────────┤
--  │ ADMIN  (acesso total)            │ admin@pederango.com          │ 123456      │
--  │ CLIENTE                          │ cliente@pederango.com        │ 123456      │
--  │ ENTREGADOR                       │ entregador@pederango.com     │ 123456      │
--  └──────────────────────────────────┴──────────────────────────────┴─────────────┘
-- =============================================================================

-- ─── LIMPEZA TOTAL (ordem respeita FKs) ──────────────────────────────────────
TRUNCATE TABLE
    cancelamentos_pedido,
    itens_pedido,
    pedidos,
    entregadores,
    clientes,
    produtos,
    usuarios
RESTART IDENTITY CASCADE;

-- ─── 1. USUÁRIOS ─────────────────────────────────────────────────────────────
-- Hash BCrypt de '123456': $2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em) VALUES
    ('Administrador do Sistema',  'admin@pederango.com',      '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'ADMIN',      TRUE, NOW(), NOW()),
    ('Carlos Cliente',            'cliente@pederango.com',    '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'CLIENTE',    TRUE, NOW(), NOW()),
    ('João Entregador',           'entregador@pederango.com', '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'ENTREGADOR', TRUE, NOW(), NOW());

-- ─── 2. PERFIL CLIENTE ────────────────────────────────────────────────────────

INSERT INTO clientes (usuario_id, cpf, telefone, endereco_padrao, ativo, criado_em, atualizado_em)
SELECT u.id,
       '123.456.789-00',
       '(11) 91111-2222',
       'Rua das Flores, 123 — Centro, São Paulo/SP',
       TRUE, NOW(), NOW()
FROM usuarios u
WHERE u.email = 'cliente@pederango.com';

-- ─── 3. PERFIL ENTREGADOR ─────────────────────────────────────────────────────

INSERT INTO entregadores (usuario_id, telefone, veiculo, placa, disponivel, ativo, criado_em, atualizado_em)
SELECT u.id,
       '(11) 98765-4321',
       'Moto Honda CG 160',
       'ABC-1234',
       TRUE, TRUE, NOW(), NOW()
FROM usuarios u
WHERE u.email = 'entregador@pederango.com';

-- ─── 4. CARDÁPIO DE PRODUTOS ──────────────────────────────────────────────────

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em) VALUES
    ('X-Burguer Especial',
     'Pão brioche tostado, hambúrguer artesanal 180g, queijo cheddar, alface, tomate e maionese da casa.',
     18.00, 'Lanches', TRUE, NOW(), NOW()),

    ('X-Bacon Duplo',
     'Pão brioche, dois hambúrgueres 150g, bacon crocante, queijo prato e molho barbecue.',
     24.90, 'Lanches', TRUE, NOW(), NOW()),

    ('Pizza Calabresa Grande',
     'Massa artesanal, molho de tomate, muçarela, calabresa fatiada, cebola e azeitonas.',
     49.90, 'Pizzas', TRUE, NOW(), NOW()),

    ('Pizza Margherita',
     'Molho de tomate italiano, muçarela de búfala, tomate cereja e manjericão fresco.',
     47.00, 'Pizzas', TRUE, NOW(), NOW()),

    ('Batata Frita Rústica',
     'Porção 300g de batata rústica com casca, temperada com ervas e acompanha molho cheddar.',
     18.50, 'Acompanhamentos', TRUE, NOW(), NOW()),

    ('Onion Rings',
     'Anéis de cebola empanados e fritos na hora, servidos com molho ranch.',
     14.00, 'Acompanhamentos', TRUE, NOW(), NOW()),

    ('Refrigerante Lata 350ml',
     'Escolha entre Coca-Cola, Guaraná Antarctica ou Sprite — bem gelado.',
     7.00, 'Bebidas', TRUE, NOW(), NOW()),

    ('Petit Gâteau de Chocolate',
     'Bolo quente de chocolate belga com centro cremoso, acompanha bola de sorvete de creme.',
     22.00, 'Sobremesas', TRUE, NOW(), NOW());

-- ─── CONFIRMAÇÃO ─────────────────────────────────────────────────────────────
DO $$
DECLARE
    v_usuarios   INTEGER;
    v_clientes   INTEGER;
    v_entregadores INTEGER;
    v_produtos   INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_usuarios    FROM usuarios;
    SELECT COUNT(*) INTO v_clientes    FROM clientes;
    SELECT COUNT(*) INTO v_entregadores FROM entregadores;
    SELECT COUNT(*) INTO v_produtos    FROM produtos;

    RAISE NOTICE '====================================================';
    RAISE NOTICE 'Seed concluído com sucesso!';
    RAISE NOTICE '  Usuários criados   : %', v_usuarios;
    RAISE NOTICE '  Clientes vinculados: %', v_clientes;
    RAISE NOTICE '  Entregadores       : %', v_entregadores;
    RAISE NOTICE '  Produtos no cardápio: %', v_produtos;
    RAISE NOTICE '====================================================';
    RAISE NOTICE 'Acesse: http://localhost:5173  (front-end)';
    RAISE NOTICE 'API:    http://localhost:8100  (back-end)';
    RAISE NOTICE 'Swagger:http://localhost:8100/swagger-ui.html';
    RAISE NOTICE '====================================================';
END;
$$;
