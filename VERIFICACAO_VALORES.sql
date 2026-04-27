-- ================================================
-- SCRIPT DE VERIFICAÇÃO E DEBUG DOS VALORES
-- Execute para verificar se os valores estão sendo lidos
-- ================================================

USE gestor_estoque;

-- ============ VERIFICAÇÃO 1: Movimentações com valores ============
SELECT 'VERIFICAÇÃO 1: Movimentações com valores' as test;

SELECT
    m.id_movimentacao,
    m.id_produto,
    p.nome_produto,
    m.quantidade_movimentacao,
    m.valor_unitario,
    (m.quantidade_movimentacao * m.valor_unitario) as valor_total_calculado,
    m.data_movimentacao,
    m.tipo_movimentacao
FROM movimentacoes m
JOIN produtos p ON m.id_produto = p.id_produto
WHERE m.data_movimentacao >= '2026-04-13' AND m.data_movimentacao <= '2026-04-26'
ORDER BY m.data_movimentacao DESC
LIMIT 20;

-- ============ VERIFICAÇÃO 2: Contagem de movimentações ============
SELECT 'VERIFICAÇÃO 2: Total de movimentações' as test;
SELECT COUNT(*) as total_movimentacoes FROM movimentacoes;

-- ============ VERIFICAÇÃO 3: Movimentações onde valor_unitario é NULL ============
SELECT 'VERIFICAÇÃO 3: Movimentações com valor_unitario NULL' as test;
SELECT
    m.id_movimentacao,
    p.nome_produto,
    m.valor_unitario,
    m.quantidade_movimentacao
FROM movimentacoes m
JOIN produtos p ON m.id_produto = p.id_produto
WHERE m.valor_unitario IS NULL
LIMIT 10;

-- ============ VERIFICAÇÃO 4: Totalizações por tipo ============
SELECT 'VERIFICAÇÃO 4: Totalizações por tipo' as test;
SELECT
    m.tipo_movimentacao,
    COUNT(*) as quantidade_movimentacoes,
    SUM(m.quantidade_movimentacao) as quantidade_total,
    SUM(m.quantidade_movimentacao * m.valor_unitario) as valor_total
FROM movimentacoes m
WHERE m.valor_unitario IS NOT NULL
GROUP BY m.tipo_movimentacao;

-- ============ VERIFICAÇÃO 5: Por fornecedor ============
SELECT 'VERIFICAÇÃO 5: Totalizações por fornecedor' as test;
SELECT
    f.nome_fornecedor,
    COUNT(m.id_movimentacao) as total_movimentacoes,
    COUNT(DISTINCT m.id_produto) as total_produtos,
    SUM(m.quantidade_movimentacao * m.valor_unitario) as valor_total
FROM movimentacoes m
JOIN fornecedores f ON m.id_fornecedor = f.id_fornecedor
WHERE m.valor_unitario IS NOT NULL
GROUP BY f.nome_fornecedor
ORDER BY valor_total DESC;

-- ============ VERIFICAÇÃO 6: Por produto ============
SELECT 'VERIFICAÇÃO 6: Totalizações por produto' as test;
SELECT
    p.nome_produto,
    SUM(CASE WHEN m.tipo_movimentacao = 'ENTRADA' THEN m.quantidade_movimentacao ELSE 0 END) as total_entradas,
    SUM(CASE WHEN m.tipo_movimentacao = 'SAIDA' THEN m.quantidade_movimentacao ELSE 0 END) as total_saidas,
    SUM(CASE WHEN m.tipo_movimentacao = 'AJUSTE' THEN m.quantidade_movimentacao ELSE 0 END) as total_ajustes,
    p.quantidade as estoque_atual,
    SUM(m.quantidade_movimentacao * m.valor_unitario) as valor_total_movimentado
FROM movimentacoes m
JOIN produtos p ON m.id_produto = p.id_produto
WHERE m.valor_unitario IS NOT NULL
GROUP BY p.id_produto, p.nome_produto, p.quantidade
ORDER BY valor_total_movimentado DESC;

-- ============ VERIFICAÇÃO 7: Estrutura das tabelas ============
SELECT 'VERIFICAÇÃO 7: Estrutura de movimentacoes' as test;
DESCRIBE movimentacoes;

-- ============ VERIFICAÇÃO 8: Amostra de dados brutos ============
SELECT 'VERIFICAÇÃO 8: Dados brutos das 5 primeiras movimentações' as test;
SELECT * FROM movimentacoes LIMIT 5;

