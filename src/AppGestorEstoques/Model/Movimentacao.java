package AppGestorEstoques.Model;

import  java.sql.Timestamp;
import java.text.Normalizer;
import java.util.Locale;

public record Movimentacao
        (
                int idMovimentacao,
                int idUser,
                int qtdMovimentacao,
                TipoMovimentacao tipoMovimentacao,
                Timestamp dataMovimentacao
        )
        {
            public enum TipoMovimentacao{
                ENTRADA,
                SAIDA,
                AJUSTE;

                public static TipoMovimentacao fromText(String valor) {
                    if (valor == null || valor.trim().isEmpty()) {
                        throw new IllegalArgumentException("Preencha o tipo de movimentacao.");
                    }

                    String normalizado = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")
                            .toUpperCase(Locale.ROOT);

                    return switch (normalizado) {
                        case "ENTRADA" -> ENTRADA;
                        case "SAIDA" -> SAIDA;
                        case "AJUSTE" -> AJUSTE;
                        default -> throw new IllegalArgumentException("Tipo de movimentacao invalido. Use ENTRADA, SAIDA ou AJUSTE.");
                    };
                }
            }

            public Movimentacao{
                if(idUser<=0)                    throw new IllegalArgumentException("ID de usuario invalido.");
                if(tipoMovimentacao==null) throw new IllegalArgumentException("Preencha o tipo de movimentacao.");
                if (qtdMovimentacao<=0) throw new IllegalArgumentException("Preencha o qtd de movimentacao.");
            }
        }
