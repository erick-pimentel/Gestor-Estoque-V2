package com.gestorestoques.api.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gestorestoques.api.config.TipoMovimentacaoDeserializer;

@JsonDeserialize(using = TipoMovimentacaoDeserializer.class)
public enum TipoMovimentacao {
    ENTRADA,
    SAIDA,
    AJUSTE
}

