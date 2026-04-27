package com.gestorestoques.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fornecedores")
public class Fornecedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fornecedor")
    private Integer idFornecedor;

    @Column(name = "nome_fornecedor", nullable = false)
    private String nomeFornecedor;

    @Column(name = "cnpj_fornecedor", nullable = false, unique = true)
    private String cnpj;

    // Compatibilidade com schema legado que ainda exige a coluna `cnpj` como NOT NULL.
    @Column(name = "cnpj", nullable = false)
    private String cnpjLegado;

    @Column(name = "contato_fornecedor")
    private String contato;

    @Column(name = "email_fornecedor")
    private String email;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    public Integer getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Integer idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
        this.cnpjLegado = cnpj;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @PrePersist
    public void prePersist() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
        if (cnpjLegado == null || cnpjLegado.isBlank()) {
            cnpjLegado = cnpj;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (cnpjLegado == null || cnpjLegado.isBlank()) {
            cnpjLegado = cnpj;
        }
    }
}

