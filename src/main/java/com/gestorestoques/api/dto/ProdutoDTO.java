package com.gestorestoques.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProdutoDTO {
    @NotBlank(message = "Nome do produto e obrigatorio")
    @Size(max = 120, message = "Nome do produto deve ter no maximo 120 caracteres")
    private String nome;

    @NotBlank(message = "Codigo do produto e obrigatorio")
    @Size(max = 40, message = "Codigo do produto deve ter no maximo 40 caracteres")
    private String codigo;

    @NotNull(message = "Quantidade e obrigatoria")
    @PositiveOrZero(message = "Quantidade nao pode ser negativa")
    private Integer quantidade;

    @NotNull(message = "Preco de custo e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preco de custo deve ser maior que zero")
    private BigDecimal precoCusto;

    @NotNull(message = "Preco de venda e obrigatorio")
    @DecimalMin(value = "0.01", message = "Preco de venda deve ser maior que zero")
    private BigDecimal precoVenda;

    private LocalDate validade;

    @NotNull(message = "Fornecedor e obrigatorio")
    @Positive(message = "Fornecedor invalido")
    private Integer idFornecedor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(BigDecimal precoCusto) {
        this.precoCusto = precoCusto;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }
}

