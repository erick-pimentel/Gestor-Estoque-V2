package com.gestorestoques.api.dto;

import com.gestorestoques.api.model.TipoMovimentacao;

import java.math.BigDecimal;

public class RelatorioSemanalDTO {
    private String semana;
    private String nomeProduto;
    private TipoMovimentacao tipo;
    private Integer quantidade;
    private BigDecimal valorTotal;
    private Integer totalEntradas;
    private Integer totalSaidas;
    private Integer totalMovimentacoes;

    public String getSemana() {
        return semana;
    }

    public void setSemana(String semana) {
        this.semana = semana;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getTotalEntradas() {
        return totalEntradas;
    }

    public void setTotalEntradas(Integer totalEntradas) {
        this.totalEntradas = totalEntradas;
    }

    public Integer getTotalSaidas() {
        return totalSaidas;
    }

    public void setTotalSaidas(Integer totalSaidas) {
        this.totalSaidas = totalSaidas;
    }

    public Integer getTotalMovimentacoes() {
        return totalMovimentacoes;
    }

    public void setTotalMovimentacoes(Integer totalMovimentacoes) {
        this.totalMovimentacoes = totalMovimentacoes;
    }
}

