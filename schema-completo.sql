-- ================================================
-- SCRIPT DE CRIAÇÃO COMPLETO DO SCHEMA
-- Garante que todas as tabelas tenham as colunas corretas
-- ================================================

-- Criar banco de dados se não existir
CREATE DATABASE IF NOT EXISTS gestor_estoque;
USE gestor_estoque;

-- ============ TABELA DE USUÁRIOS ============
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome_usuario VARCHAR(100) NOT NULL UNIQUE,
    senha_usuario VARCHAR(255) NOT NULL,
    perfil_usuario VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE FORNECEDORES ============
CREATE TABLE IF NOT EXISTS fornecedores (
    id_fornecedor INT AUTO_INCREMENT PRIMARY KEY,
    nome_fornecedor VARCHAR(150) NOT NULL,
    cnpj_fornecedor VARCHAR(20) UNIQUE,
    email_fornecedor VARCHAR(100),
    telefone_fornecedor VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE PRODUTOS ============
CREATE TABLE IF NOT EXISTS produtos (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome_produto VARCHAR(150) NOT NULL,
    codigo_produto VARCHAR(50) NOT NULL UNIQUE,
    quantidade INT NOT NULL DEFAULT 0,
    preco_custo DECIMAL(10, 2) NOT NULL DEFAULT 0,
    preco_venda DECIMAL(10, 2) NOT NULL DEFAULT 0,
    validade DATE,
    id_fornecedor INT,
    id_usuario INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedores(id_fornecedor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ TABELA DE MOVIMENTAÇÕES ============
CREATE TABLE IF NOT EXISTS movimentacoes (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT NOT NULL,
    id_usuario INT NOT NULL,
    id_fornecedor INT,
    tipo_movimentacao VARCHAR(20) NOT NULL,
    quantidade_movimentacao INT NOT NULL,
    quantidade INT,
    valor_unitario DECIMAL(10, 2),
    data_movimentacao DATETIME NOT NULL,
    observacao TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedores(id_fornecedor),
    INDEX idx_data (data_movimentacao),
    INDEX idx_produto (id_produto),
    INDEX idx_tipo (tipo_movimentacao)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ POPULANDO DADOS ============

-- Inserir usuários
INSERT INTO usuarios (nome_usuario, senha_usuario, perfil_usuario) VALUES
('admin', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Administrador'),
('gerente', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Gestor'),
('operador', 'e7cf3ef4f17c3999a94f2c6f612e8a888e5b1026', 'Operador de Estoque')
ON DUPLICATE KEY UPDATE id_usuario=id_usuario;

-- Inserir fornecedores
INSERT INTO fornecedores (nome_fornecedor, cnpj_fornecedor, email_fornecedor, telefone_fornecedor) VALUES
('Distribuidora ABC', '12.345.678/0001-90', 'contato@abc.com.br', '11 98765-4321'),
('Fornecedor XYZ', '98.765.432/0001-10', 'vendas@xyz.com.br', '21 99876-5432'),
('Importadora Premium', '11.111.111/0001-11', 'import@premium.com.br', '85 98888-7777'),
('Fabricante Local', '22.222.222/0001-22', 'fabrica@local.com.br', '47 99999-8888')
ON DUPLICATE KEY UPDATE id_fornecedor=id_fornecedor;

-- Inserir produtos
INSERT INTO produtos (nome_produto, codigo_produto, quantidade, preco_custo, preco_venda, id_fornecedor, id_usuario) VALUES
('Notebook Dell Inspiron 15', 'NB-DELL-001', 15, 2500.00, 3200.00, 1, 1),
('Mouse Logitech MX Master', 'MO-LOG-001', 45, 150.00, 250.00, 1, 1),
('Teclado Mecanico RGB', 'TEC-RGB-001', 25, 350.00, 600.00, 2, 1),
('Monitor LG 24 FHD', 'MON-LG-001', 8, 800.00, 1200.00, 1, 1),
('Webcam HD 1080p', 'WEB-HD-001', 30, 120.00, 199.90, 3, 1),
('SSD Samsung 1TB', 'SSD-SAM-001', 20, 400.00, 650.00, 2, 1),
('Headset Corsair', 'HEAD-COR-001', 12, 280.00, 450.00, 4, 1),
('Mousepad Gamer', 'PAD-GAM-001', 50, 30.00, 79.90, 3, 1),
('Hub USB 3.0', 'HUB-USB-001', 35, 80.00, 150.00, 2, 1),
('Cabo HDMI 2m', 'CABO-HDMI-001', 60, 15.00, 35.00, 4, 1)
ON DUPLICATE KEY UPDATE id_produto=id_produto;

-- Inserir movimentações (Semana passada: 2026-04-13 a 2026-04-19)
INSERT INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(1, 1, 1, 'ENTRADA', 5, 5, 3200.00, '2026-04-14 10:30:00', 'Compra lote 001'),
(2, 1, 1, 'ENTRADA', 20, 20, 250.00, '2026-04-15 14:00:00', 'Reabastecimento'),
(3, 1, 2, 'ENTRADA', 10, 10, 600.00, '2026-04-16 09:00:00', 'Pedido 002'),
(5, 1, 3, 'ENTRADA', 15, 15, 199.90, '2026-04-17 11:30:00', 'Stock inicial'),
(4, 1, 1, 'SAIDA', 2, 2, 1200.00, '2026-04-18 13:00:00', 'Venda cliente'),
(6, 1, 2, 'ENTRADA', 8, 8, 650.00, '2026-04-19 10:15:00', 'Reposicao');

-- Inserir movimentações (Semana atual: 2026-04-20 a 2026-04-26)
INSERT INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(2, 1, 1, 'SAIDA', 5, 5, 250.00, '2026-04-20 09:30:00', 'Vendas diretas'),
(7, 1, 4, 'ENTRADA', 10, 10, 450.00, '2026-04-20 15:00:00', 'Novo item em estoque'),
(1, 1, 1, 'SAIDA', 3, 3, 3200.00, '2026-04-21 10:00:00', 'Venda corporativa'),
(8, 1, 3, 'ENTRADA', 30, 30, 79.90, '2026-04-21 16:30:00', 'Complemento estoque'),
(9, 1, 2, 'ENTRADA', 20, 20, 150.00, '2026-04-22 11:00:00', 'Acessorios em geral'),
(3, 1, 2, 'SAIDA', 3, 3, 600.00, '2026-04-22 14:30:00', 'Venda loja'),
(10, 1, 4, 'ENTRADA', 40, 40, 35.00, '2026-04-23 08:00:00', 'Stock cabos'),
(5, 1, 3, 'SAIDA', 8, 8, 199.90, '2026-04-23 13:00:00', 'Saida venda'),
(6, 1, 2, 'AJUSTE', 5, 5, 650.00, '2026-04-24 10:00:00', '[ESTOQUE_ANTERIOR=8] Contagem fisica');

-- Inserir movimentações (Próxima semana: 2026-04-27 em diante - futura)
INSERT INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(4, 1, 1, 'ENTRADA', 3, 3, 1200.00, '2026-04-27 09:00:00', 'Pedido antecipado'),
(1, 1, 1, 'SAIDA', 2, 2, 3200.00, '2026-04-28 11:30:00', 'Venda online'),
(7, 1, 4, 'ENTRADA', 5, 5, 450.00, '2026-04-29 14:00:00', 'Complemento'),
(2, 1, 1, 'AJUSTE', 25, 25, 250.00, '2026-04-30 10:00:00', '[ESTOQUE_ANTERIOR=40] Contagem inventario');

-- Inserir dados históricos (abril anterior - 01/04 a 10/04)
INSERT INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
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

-- Inserir saidas no periodo histórico
INSERT INTO movimentacoes (id_produto, id_usuario, id_fornecedor, tipo_movimentacao, quantidade_movimentacao, quantidade, valor_unitario, data_movimentacao, observacao) VALUES
(1, 1, 1, 'SAIDA', 1, 1, 3200.00, '2026-04-11 10:30:00', 'Venda 001'),
(2, 1, 1, 'SAIDA', 8, 8, 250.00, '2026-04-11 14:00:00', 'Vendas variadas'),
(3, 1, 2, 'SAIDA', 2, 2, 600.00, '2026-04-12 11:00:00', 'Saida estoque'),
(4, 1, 1, 'SAIDA', 1, 1, 1200.00, '2026-04-12 15:30:00', 'Venda corporativa'),
(5, 1, 3, 'SAIDA', 3, 3, 199.90, '2026-04-13 09:00:00', 'Saida loja');

-- ============ VERIFICAÇÃO ============
SELECT COUNT(*) as total_movimentacoes FROM movimentacoes;
SELECT COUNT(*) as total_produtos FROM produtos;
SELECT COUNT(*) as total_fornecedores FROM fornecedores;
SELECT COUNT(*) as total_usuarios FROM usuarios;

-- Verificar dados das movimentações
SELECT 
    m.id_movimentacao,
    p.nome_produto,
    m.tipo_movimentacao,
    m.quantidade_movimentacao,
    m.valor_unitario,
    m.data_movimentacao
FROM movimentacoes m
JOIN produtos p ON m.id_produto = p.id_produto
LIMIT 10;
