package com.gestorestoques.api.dto;

import java.math.BigDecimal;

public class RelatorioProdutoDTO {
    private Integer idProduto;
    private String nomeProduto;
    private Integer totalEntradas;
    private Integer totalSaidas;
    private Integer totalAjustes;
    private Integer quantidadeAtual;
    private BigDecimal valorMovimentado;

    public RelatorioProdutoDTO() {}

    public RelatorioProdutoDTO(Integer idProduto, String nomeProduto, Integer totalEntradas,
                               Integer totalSaidas, Integer totalAjustes, Integer quantidadeAtual,
                               BigDecimal valorMovimentado) {
        this.idProduto = idProduto;
        this.nomeProduto = nomeProduto;
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.totalAjustes = totalAjustes;
        this.quantidadeAtual = quantidadeAtual;
        this.valorMovimentado = valorMovimentado;
    }

    public Integer getIdProduto() { return idProduto; }
    public void setIdProduto(Integer idProduto) { this.idProduto = idProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Integer getTotalEntradas() { return totalEntradas; }
    public void setTotalEntradas(Integer totalEntradas) { this.totalEntradas = totalEntradas; }

    public Integer getTotalSaidas() { return totalSaidas; }
    public void setTotalSaidas(Integer totalSaidas) { this.totalSaidas = totalSaidas; }

    public Integer getTotalAjustes() { return totalAjustes; }
    public void setTotalAjustes(Integer totalAjustes) { this.totalAjustes = totalAjustes; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public BigDecimal getValorMovimentado() { return valorMovimentado; }
    public void setValorMovimentado(BigDecimal valorMovimentado) { this.valorMovimentado = valorMovimentado; }
}

