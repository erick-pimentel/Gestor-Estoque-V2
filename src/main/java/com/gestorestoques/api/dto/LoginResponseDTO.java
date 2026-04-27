package com.gestorestoques.api.dto;

public class LoginResponseDTO {
    private String token;
    private String perfil;
    private String nomeUsuario;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String token, String perfil, String nomeUsuario) {
        this.token = token;
        this.perfil = perfil;
        this.nomeUsuario = nomeUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}

