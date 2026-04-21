package com.gestorestoques.api.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PerfilUsuarioConverter implements AttributeConverter<PerfilUsuario, String> {

    @Override
    public String convertToDatabaseColumn(PerfilUsuario attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public PerfilUsuario convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }

        try {
            return PerfilUsuario.valueOf(dbData);
        } catch (IllegalArgumentException ignored) {
            return PerfilUsuario.fromDescricao(dbData);
        }
    }
}

