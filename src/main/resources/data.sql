-- ============================================
-- SCRIPT DE POPULAÇÃO DO BANCO DE DADOS
-- Para testar os relatórios do sistema
-- ============================================

-- Limpar dados existentes se necessário
-- TRUNCATE TABLE movimentacoes;
-- TRUNCATE TABLE produtos;
-- TRUNCATE TABLE fornecedores;
-- TRUNCATE TABLE usuarios;

-- ============ INSERIR USUÁRIOS ============
INSERT IGNORE INTO usuarios (id_usuario, nome_usuario, senha_usuario, perfil_usuario) VALUES
(1, 'admin', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Administrador'),
(2, 'gerente', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Gestor'),
(3, 'operador', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Operador de Estoque');

-- ============ INSERIR FORNECEDORES ============
INSERT IGNORE INTO fornecedores (id_fornecedor, nome_fornecedor, cnpj_fornecedor, email_fornecedor, telefone_fornecedor) VALUES
(1, 'Distribuidora ABC', '12.345.678/0001-90', 'contato@abc.com.br', '11 98765-4321'),
(2, 'Fornecedor XYZ', '98.765.432/0001-10', 'vendas@xyz.com.br', '21 99876-5432'),
(3, 'Importadora Premium', '11.111.111/0001-11', 'import@premium.com.br', '85 98888-7777'),
(4, 'Fabricante Local', '22.222.222/0001-22', 'fabrica@local.com.br', '47 99999-8888');

-- ============ INSERIR PRODUTOS ============
INSERT IGNORE INTO produtos (id_produto, nome_produto, codigo_produto, quantidade, preco_custo, preco_venda, id_fornecedor, id_usuario) VALUES
(1, 'Notebook Dell Inspiron 15', 'NB-DELL-001', 15, 2500.00, 3200.00, 1, 1),
(2, 'Mouse Logitech MX Master', 'MO-LOG-001', 45, 150.00, 250.00, 1, 1),
(3, 'Teclado Mecanico RGB', 'TEC-RGB-001', 25, 350.00, 600.00, 2, 1),
(4, 'Monitor LG 24 FHD', 'MON-LG-001', 8, 800.00, 1200.00, 1, 1),
(5, 'Webcam HD 1080p', 'WEB-HD-001', 30, 120.00, 199.90, 3, 1),
(6, 'SSD Samsung 1TB', 'SSD-SAM-001', 20, 400.00, 650.00, 2, 1),
(7, 'Headset Corsair', 'HEAD-COR-001', 12, 280.00, 450.00, 4, 1),
(8, 'Mousepad Gamer', 'PAD-GAM-001', 50, 30.00, 79.90, 3, 1),
(9, 'Hub USB 3.0', 'HUB-USB-001', 35, 80.00, 150.00, 2, 1),
(10, 'Cabo HDMI 2m', 'CABO-HDMI-001', 60, 15.00, 35.00, 4, 1);

-- ============ INSERIR MOVIMENTAÇÕES ============

-- Semana passada: 2026-04-13 a 2026-04-19
INSERT IGNORE INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(1, 1, 1, 'ENTRADA', 5, 5, 3200.00, '2026-04-14 10:30:00', 'Compra lote 001'),
(2, 1, 1, 'ENTRADA', 20, 20, 250.00, '2026-04-15 14:00:00', 'Reabastecimento'),
(3, 1, 2, 'ENTRADA', 10, 10, 600.00, '2026-04-16 09:00:00', 'Pedido 002'),
(5, 1, 3, 'ENTRADA', 15, 15, 199.90, '2026-04-17 11:30:00', 'Stock inicial'),
(4, 1, 1, 'SAIDA', 2, 2, 1200.00, '2026-04-18 13:00:00', 'Venda cliente'),
(6, 1, 2, 'ENTRADA', 8, 8, 650.00, '2026-04-19 10:15:00', 'Reposicao');

-- Semana atual: 2026-04-20 a 2026-04-26
INSERT IGNORE INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(2, 1, 1, 'SAIDA', 5, 5, 250.00, '2026-04-20 09:30:00', 'Vendas diretas'),
(7, 1, 4, 'ENTRADA', 10, 10, 450.00, '2026-04-20 15:00:00', 'Novo item em estoque'),
(1, 1, 1, 'SAIDA', 3, 3, 3200.00, '2026-04-21 10:00:00', 'Venda corporativa'),
(8, 1, 3, 'ENTRADA', 30, 30, 79.90, '2026-04-21 16:30:00', 'Complemento estoque'),
(9, 1, 2, 'ENTRADA', 20, 20, 150.00, '2026-04-22 11:00:00', 'Acessorios em geral'),
(3, 1, 2, 'SAIDA', 3, 3, 600.00, '2026-04-22 14:30:00', 'Venda loja'),
(10, 1, 4, 'ENTRADA', 40, 40, 35.00, '2026-04-23 08:00:00', 'Stock cabos'),
(5, 1, 3, 'SAIDA', 8, 8, 199.90, '2026-04-23 13:00:00', 'Saida venda'),
(6, 1, 2, 'AJUSTE', 5, 5, 650.00, '2026-04-24 10:00:00', '[ESTOQUE_ANTERIOR=8] Contagem fisica');

-- Próxima semana: 2026-04-27 em diante (futura)
INSERT IGNORE INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(4, 1, 1, 'ENTRADA', 3, 3, 1200.00, '2026-04-27 09:00:00', 'Pedido antecipado'),
(1, 1, 1, 'SAIDA', 2, 2, 3200.00, '2026-04-28 11:30:00', 'Venda online'),
(7, 1, 4, 'ENTRADA', 5, 5, 450.00, '2026-04-29 14:00:00', 'Complemento'),
(2, 1, 1, 'AJUSTE', 25, 25, 250.00, '2026-04-30 10:00:00', '[ESTOQUE_ANTERIOR=40] Contagem inventario');

-- Dados históricos (abril anterior)
INSERT IGNORE INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(1, 1, 1, 'ENTRADA', 10, 10, 3200.00, '2026-04-01 08:00:00', 'Pedido inicial'),
(2, 1, 1, 'ENTRADA', 30, 30, 250.00, '2026-04-02 10:00:00', 'Stock inicial'),
(3, 1, 2, 'ENTRADA', 15, 15, 600.00, '2026-04-03 09:30:00', 'Reabastecimento'),
(4, 1, 1, 'ENTRADA', 5, 5, 1200.00, '2026-04-04 14:00:00', 'Compra especial'),
(5, 1, 3, 'ENTRADA', 20, 20, 199.90, '2026-04-05 11:00:00', 'Lote A'),
(6, 1, 2, 'ENTRADA', 12, 12, 650.00, '2026-04-06 13:30:00', 'Stock SSD'),
(7, 1, 4, 'ENTRADA', 8, 8, 450.00, '2026-04-07 10:00:00', 'Novo produto'),
(8, 1, 3, 'ENTRADA', 40, 40, 79.90, '2026-04-08 15:00:00', 'Acessorios'),
(9, 1, 2, 'ENTRADA', 25, 25, 150.00, '2026-04-09 09:00:00', 'Hubs'),
(10, 1, 4, 'ENTRADA', 50, 50, 35.00, '2026-04-10 16:00:00', 'Cabos iniciais');

-- Saidas no periodo histórico
INSERT IGNORE INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(1, 1, 1, 'SAIDA', 1, 1, 3200.00, '2026-04-11 10:30:00', 'Venda 001'),
(2, 1, 1, 'SAIDA', 8, 8, 250.00, '2026-04-11 14:00:00', 'Vendas variadas'),
(3, 1, 2, 'SAIDA', 2, 2, 600.00, '2026-04-12 11:00:00', 'Saida estoque'),
(4, 1, 1, 'SAIDA', 1, 1, 1200.00, '2026-04-12 15:30:00', 'Venda corporativa'),
(5, 1, 3, 'SAIDA', 3, 3, 199.90, '2026-04-13 09:00:00', 'Saida loja');

-- ============ FIM DO SCRIPT ============

