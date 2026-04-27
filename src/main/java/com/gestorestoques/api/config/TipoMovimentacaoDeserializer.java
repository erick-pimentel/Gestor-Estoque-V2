package com.gestorestoques.api.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gestorestoques.api.model.TipoMovimentacao;

import java.io.IOException;

public class TipoMovimentacaoDeserializer extends JsonDeserializer<TipoMovimentacao> {

    @Override
    public TipoMovimentacao deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return TipoMovimentacao.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IOException("Tipo de movimentacao invalido: " + value, e);
        }
    }
}

