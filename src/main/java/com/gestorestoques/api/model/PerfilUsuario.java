package com.gestorestoques.api.model;

import java.util.Arrays;

public enum PerfilUsuario {
    ADMINISTRADOR("Administrador"),
    OPERADOR_DE_ESTOQUE("Operador de Estoque"),
    GESTOR("Gestor");

    private final String descricao;

    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static PerfilUsuario fromDescricao(String descricao) {
        return Arrays.stream(values())
                .filter(v -> v.descricao.equalsIgnoreCase(descricao) || v.name().equalsIgnoreCase(descricao))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Perfil invalido: " + descricao));
    }
}

