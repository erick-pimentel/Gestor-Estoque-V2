package AppGestorEstoques.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


public record RelatorioSemanalDTO
        (
            LocalDate periodoInicio,
            LocalDate periodoFim,
            int totalProdutos,
            int produtosVendidos,
            BigDecimal faturamento,
            List<MovimentacaoDetalhesDTO> detalhes
        ) { }
