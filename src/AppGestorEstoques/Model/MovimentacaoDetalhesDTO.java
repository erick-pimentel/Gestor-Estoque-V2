package AppGestorEstoques.Model;

import java.time.LocalDateTime;

public record MovimentacaoDetalhesDTO
        (
            int idMovimentacao,
            int qtd,
            String produtoNome,
            String tipo,
            LocalDateTime data
        ) { }
