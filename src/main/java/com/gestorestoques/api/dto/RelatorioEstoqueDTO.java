package com.gestorestoques.api.dto;

import java.math.BigDecimal;

public class RelatorioEstoqueDTO {
    private Integer idProduto;
    private String nomeProduto;
    private Integer quantidadeAtual;
    private BigDecimal precoVenda;
    private BigDecimal valorTotalEstoque;
    private String codigoProduto;

    public RelatorioEstoqueDTO() {}

    public RelatorioEstoqueDTO(Integer idProduto, String nomeProduto, Integer quantidadeAtual, 
                                BigDecimal precoVenda, String codigoProduto) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.quantidadeAtual = quantidadeAtual;
        this.precoVenda = precoVenda;
        this.codigoProduto = codigoProduto;
        this.valorTotalEstoque = precoVenda != null && quantidadeAtual != null
            ? precoVenda.multiply(BigDecimal.valueOf(quantidadeAtual))
            : BigDecimal.ZERO;
    }

    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public BigDecimal getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(BigDecimal precoVenda) { this.precoVenda = precoVenda; }

    public BigDecimal getValorTotalEstoque() { return valorTotalEstoque; }
    public void setValorTotalEstoque(BigDecimal valorTotalEstoque) { this.valorTotalEstoque = valorTotalEstoque; }

    public String getCodigoProduto() { return codigoProduto; }
    public void setCodigoProduto(String codigoProduto) { this.codigoProduto = codigoProduto; }
}

