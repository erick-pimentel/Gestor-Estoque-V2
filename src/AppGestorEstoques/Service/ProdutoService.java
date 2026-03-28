package AppGestorEstoques.Service;

import AppGestorEstoques.DAO.ProdutoDAO;
import AppGestorEstoques.DAO.IProdutoDAO;
import AppGestorEstoques.Model.Produto;
import AppGestorEstoques.Model.User;

import java.math.BigDecimal;
import java.sql.Date;

public class ProdutoService {
    private final IProdutoDAO dao;

    public ProdutoService() {
        this(new ProdutoDAO());
    }

    public ProdutoService(IProdutoDAO dao) {
        this.dao = dao;
    }

    public void cadastrarProduto(User user,int idFornecedor,String nome,String codigo,String quantidade,
                         String compra,String venda,String validade) {
        validarDados(nome,codigo,quantidade,compra,venda,validade);

        Produto produto = new Produto
                (
                        0,user.getId(),idFornecedor,nome,codigo,
                        Integer.parseInt(quantidade),
                        new BigDecimal(compra),
                        new BigDecimal(venda),
                        Date.valueOf(validade),
                        null
                );
        dao.cadastrar(produto);
    }

    public Produto buscarProduto(String searchInput) {
        if (searchInput == null || searchInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe codigo, nome ou ID do produto");
        }
        return dao.buscarProduto(searchInput.trim());
    }

    public void editarProduto(String searchInput, String nome, String codigo, String quantidade,
                              String compra, String venda, String validade, String idFornecedor) {
        Produto produtoAtual = buscarProduto(searchInput);
        if (produtoAtual == null) {
            throw new IllegalArgumentException("Produto '" + searchInput + "' nao encontrado");
        }

        int fornecedorId;
        try {
            fornecedorId = Integer.parseInt(idFornecedor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID do fornecedor deve ser um numero inteiro", e);
        }
        if (fornecedorId <= 0) {
            throw new IllegalArgumentException("ID do fornecedor deve ser maior que zero");
        }

        validarDados(nome, codigo, quantidade, compra, venda, validade);

        Produto produtoAtualizado = new Produto(
                produtoAtual.idProduto(),
                produtoAtual.idUsuario(),
                fornecedorId,
                nome.trim(),
                codigo.trim(),
                Integer.parseInt(quantidade),
                new BigDecimal(compra),
                new BigDecimal(venda),
                Date.valueOf(validade),
                produtoAtual.cadastroProduto()
        );

        dao.atualizar(produtoAtualizado);
    }

    public void excluirProdutoPorId(int idProduto) {
        if (idProduto <= 0) {
            throw new IllegalArgumentException("ID do produto invalido");
        }
        dao.excluir(idProduto);
    }

    public void excluirProduto(String searchInput) {
        Produto produto = buscarProduto(searchInput);
        if (produto == null) {
            throw new IllegalArgumentException("Produto '" + searchInput + "' nao encontrado");
        }
        dao.excluir(produto.idProduto());
    }

    private void validarDados(String nome,String codigo,String quantidade,String compra,String venda,String validade) {
        if (nome == null || nome.trim().isEmpty() || nome.trim().length() > 100)
            throw new IllegalArgumentException("Nome inválido!");
        if (codigo == null || codigo.trim().isEmpty() || codigo.trim().length() > 45)
            throw new IllegalArgumentException("Código inválido (1-45 caracteres!)");
        try {
            int qtd = Integer.parseInt(quantidade);
            if (qtd < 0) throw new IllegalArgumentException("Numeros maiores ou igual a zero!");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantidade deve ser um número inteiro", e);
        }
        try {
            BigDecimal valorCompra = new BigDecimal(compra);
            if (valorCompra.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalArgumentException("Valor compra deve ser maior ou igual a zero!");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor compra deve ser um número decimal(ex: 5,0)", e);
        }
        try {
            BigDecimal valorVenda = new BigDecimal(venda);
            BigDecimal valorCompra = new BigDecimal(compra);
            if (valorVenda.compareTo(valorCompra) <= 0)
                throw new IllegalArgumentException("Valor venda deve ser maior que valor compra");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor de venda deve ser um número decimal", e);
        }
        try {
            Date.valueOf(validade);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Validade deve estar no formato yyyy-MM-dd", e);
        }
    }

}
