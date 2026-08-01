-- Seed de Dados Iniciais para Teste em Português (Senha padrão para todos os usuários do seed: 123456)

-- 1. Inserir Usuários Iniciais
INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Administrador do Sistema', 'admin@fooddelivery.com', '123456', 'ADMIN', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'Carlos Cliente', 'cliente@fooddelivery.com', '123456', 'CLIENTE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'cliente@fooddelivery.com');

INSERT INTO usuarios (nome, email, senha, perfil, ativo, criado_em, atualizado_em)
SELECT 'João Entregador', 'entregador@fooddelivery.com', '123456', 'ENTREGADOR', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'entregador@fooddelivery.com');

-- 2. Inserir Entregador vinculado ao usuário de email 'entregador@fooddelivery.com'
INSERT INTO entregadores (usuario_id, telefone, veiculo, placa, disponivel, ativo, criado_em, atualizado_em)
SELECT u.id, '(11) 98765-4321', 'Moto Honda CG 160', 'ABC-1234', TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM usuarios u
WHERE u.email = 'entregador@fooddelivery.com'
  AND NOT EXISTS (SELECT 1 FROM entregadores e WHERE e.usuario_id = u.id);

-- 3. Inserir Pedido de Exemplo
INSERT INTO pedidos (codigo, nome_cliente, endereco_entrega, status, entregador_id, valor_total, criado_em, atualizado_em)
SELECT 'PED-20260801-0001', 'Carlos Cliente', 'Rua das Flores, 123 - Centro', 'RECEBIDO', NULL, 45.90, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM pedidos WHERE nome_cliente = 'Carlos Cliente' AND status = 'RECEBIDO');

-- 4. Inserir Itens do Pedido de Exemplo
INSERT INTO itens_pedido (nome, quantidade, preco_unitario, pedido_id)
SELECT 'X-Burguer Especial', 2, 18.00, p.id
FROM pedidos p
WHERE p.nome_cliente = 'Carlos Cliente' AND p.status = 'RECEBIDO'
  AND NOT EXISTS (SELECT 1 FROM itens_pedido i WHERE i.pedido_id = p.id AND i.nome = 'X-Burguer Especial');

INSERT INTO itens_pedido (nome, quantidade, preco_unitario, pedido_id)
SELECT 'Refrigerante Guaraná 2L', 1, 9.90, p.id
FROM pedidos p
WHERE p.nome_cliente = 'Carlos Cliente' AND p.status = 'RECEBIDO'
  AND NOT EXISTS (SELECT 1 FROM itens_pedido i WHERE i.pedido_id = p.id AND i.nome = 'Refrigerante Guaraná 2L');
