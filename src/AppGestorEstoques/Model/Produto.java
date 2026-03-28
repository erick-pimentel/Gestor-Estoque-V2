package AppGestorEstoques.Model;

import java.math.BigDecimal;
import java.sql.Date;

public record Produto(
        int idProduto,
        int idUsuario,
        int idFornecedor,
        String nomeProduto,
        String codigoProduto,
        int quantidadeEstoque,
        BigDecimal valorCompra,
        BigDecimal valorVenda,
        Date validadeProduto,
        Date cadastroProduto
) {
    public Produto {
        if (idUsuario <= 0) throw new IllegalArgumentException("ID usuario invalido");
        if (idFornecedor <= 0) throw new IllegalArgumentException("ID fornecedor invalido");
        if (nomeProduto == null || nomeProduto.trim().isEmpty() || nomeProduto.trim().length() > 100)
            throw new IllegalArgumentException("Nome invalido (1-100 chars)");
        if (codigoProduto == null || codigoProduto.trim().isEmpty() || codigoProduto.trim().length() > 45)
            throw new IllegalArgumentException("Codigo invalido (1-45 chars)");
        if (quantidadeEstoque < 0) throw new IllegalArgumentException("Quantidade nao negativa");
        if (valorCompra == null || valorCompra.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor compra deve ser >= 0");
        if (valorVenda == null || valorVenda.compareTo(valorCompra) < 0)
            throw new IllegalArgumentException("Valor venda deve ser >= compra");
        if (validadeProduto == null)
            throw new IllegalArgumentException("Validade obrigatoria");
        // cadastroProduto pode ser null se o banco preencher automaticamente.
    }
}