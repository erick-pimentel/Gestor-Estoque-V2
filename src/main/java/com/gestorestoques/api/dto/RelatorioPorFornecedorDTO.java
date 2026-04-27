package com.gestorestoques.api.dto;

import java.math.BigDecimal;

public class RelatorioPorFornecedorDTO {
    private Integer idFornecedor;
    private String nomeFornecedor;
    private Long totalMovimentacoes;
    private Integer totalProdutos;
    private BigDecimal valorTotalMovimentacoes;

    public RelatorioPorFornecedorDTO() {}

    public RelatorioPorFornecedorDTO(Integer idFornecedor, String nomeFornecedor, Long totalMovimentacoes, 
                                     Integer totalProdutos, BigDecimal valorTotalMovimentacoes) {
        this.idFornecedor = idFornecedor;
        this.nomeFornecedor = nomeFornecedor;
        this.totalMovimentacoes = totalMovimentacoes;
        this.totalProdutos = totalProdutos;
        this.valorTotalMovimentacoes = valorTotalMovimentacoes;
    }

    public Integer getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(Integer idFornecedor) { this.idFornecedor = idFornecedor; }

    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }

    public Long getTotalMovimentacoes() { return totalMovimentacoes; }
    public void setTotalMovimentacoes(Long totalMovimentacoes) { this.totalMovimentacoes = totalMovimentacoes; }

    public Integer getTotalProdutos() { return totalProdutos; }
    public void setTotalProdutos(Integer totalProdutos) { this.totalProdutos = totalProdutos; }

    public BigDecimal getValorTotalMovimentacoes() { return valorTotalMovimentacoes; }
    public void setValorTotalMovimentacoes(BigDecimal valorTotalMovimentacoes) { this.valorTotalMovimentacoes = valorTotalMovimentacoes; }
}

