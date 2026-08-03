-- Seed de Dados Iniciais para Teste em Português (Senha padrão para todos os usuários do seed: 123456)
-- Hash BCrypt gerado para '123456': $2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm

-- 1. Inserir Usuários Iniciais
INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Administrador do Sistema', 'admin@fooddelivery.com', '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Carlos Cliente', 'cliente@fooddelivery.com', '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'cliente@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'João Entregador', 'entregador@fooddelivery.com', '$2a$10$bJZ21OzkEHrzzlPPt62HvuEqbMXzE5vMZYcwbhAoHwEgUG4gV0YMm', 'ENTREGADOR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'entregador@fooddelivery.com');

-- 2. Inserir Cliente vinculado ao usuário de email 'cliente@fooddelivery.com'
INSERT INTO clientes (usuario_id, cpf, telefone, endereco_padrao, ativo, criado_em, atualizado_em)
SELECT u.id, '123.456.789-00', '(11) 91111-2222', 'Rua das Flores, 123 - Centro', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.email = 'cliente@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM clientes c WHERE c.usuario_id = u.id);

-- 3. Inserir Entregador vinculado ao usuário de email 'entregador@fooddelivery.com'
INSERT INTO entregadores (usuario_id, telefone, veiculo, placa, disponivel, ativo, criado_em, atualizado_em)
SELECT u.id, '(11) 98765-4321', 'Moto Honda CG 160', 'ABC-1234', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.email = 'entregador@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM entregadores e WHERE e.usuario_id = u.id);

-- 4. Inserir Produtos Padrão do Cardápio
INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'X-Burguer Especial', 'Pão brioche, hambúrguer artesanal 180g, queijo cheddar e maionese da casa', 18.00, 'Lanches', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'X-Burguer Especial');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Pizza Calabresa', 'Massa artesanal, molho de tomate, queijo muçarela, calabresa fatiada e cebola', 45.00, 'Pizzas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Pizza Calabresa');

INSERT INTO produtos (nome, descricao, preco, categoria, ativo, criado_em, atualizado_em)
SELECT 'Refrigerante Guaraná 2L', 'Garrafa Guaraná Antarctica 2 Litros', 9.90, 'Bebidas', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM produtos WHERE nome = 'Refrigerante Guaraná 2L');

-- 5. Inserir Pedido de Exemplo com vínculo a Cliente
INSERT INTO pedidos (codigo, cliente_id, nome_cliente, endereco_entrega, status, entregador_id, valor_total, criado_em, atualizado_em)
SELECT 'PED-20260801-0001', c.id, 'Carlos Cliente', 'Rua das Flores, 123 - Centro', 'RECEBIDO', NULL, 45.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM clientes c
JOIN usuarios u ON c.usuario_id = u.id
WHERE u.email = 'cliente@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM pedidos WHERE codigo = 'PED-20260801-0001');

-- 6. Inserir Itens do Pedido com vínculo a Produtos do Cardápio
INSERT INTO itens_pedido (produto_id, nome, quantidade, preco_unitario, pedido_id)
SELECT pr.id, pr.nome, 2, pr.preco, p.id
FROM pedidos p
JOIN produtos pr ON pr.nome = 'X-Burguer Especial'
WHERE p.codigo = 'PED-20260801-0001'
  AND NOT EXISTS (SELECT 1 FROM itens_pedido i WHERE i.pedido_id = p.id AND i.nome = 'X-Burguer Especial');

INSERT INTO itens_pedido (produto_id, nome, quantidade, preco_unitario, pedido_id)
SELECT pr.id, pr.nome, 1, pr.preco, p.id
FROM pedidos p
JOIN produtos pr ON pr.nome = 'Refrigerante Guaraná 2L'
WHERE p.codigo = 'PED-20260801-0001'
  AND NOT EXISTS (SELECT 1 FROM itens_pedido i WHERE i.pedido_id = p.id AND i.nome = 'Refrigerante Guaraná 2L');

-- 7. Criar registro na tabela 'clientes' para qualquer usuário com perfil CLIENTE que não possua registro correspondente (ex: usuário ID 5)
INSERT INTO clientes (usuario_id, ativo, criado_em, atualizado_em)
SELECT u.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.perfil = 'CLIENTE'
  AND NOT EXISTS (SELECT 1 FROM clientes c WHERE c.usuario_id = u.id);

-- 8. Vincular pedidos sem cliente_id aos respectivos registros da tabela clientes com base no nome do cliente
UPDATE pedidos p
SET cliente_id = c.id
FROM clientes c
JOIN usuarios u ON c.usuario_id = u.id
WHERE p.cliente_id IS NULL
  AND LOWER(TRIM(p.nome_cliente)) = LOWER(TRIM(u.nome));
