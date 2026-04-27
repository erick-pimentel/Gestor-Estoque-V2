package com.gestorestoques.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FornecedorDTO {
    @NotBlank(message = "Nome do fornecedor e obrigatorio")
    @Size(min = 2, max = 120, message = "Nome do fornecedor deve ter entre 2 e 120 caracteres")
    private String nome;

    @NotBlank(message = "CNPJ e obrigatorio")
    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "Formato de CNPJ invalido. Use XX.XXX.XXX/XXXX-XX")
    private String cnpj;

    @Size(max = 120, message = "Contato deve ter no maximo 120 caracteres")
    private String contato;

    @Size(max = 120, message = "Email deve ter no maximo 120 caracteres")
    @Email(message = "Email invalido")
    private String email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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
}

