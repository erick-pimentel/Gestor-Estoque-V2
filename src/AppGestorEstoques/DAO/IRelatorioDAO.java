package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.MovimentacaoDetalhesDTO;
import AppGestorEstoques.Model.RelatorioSemanalDTO;

import java.time.LocalDate;
import java.util.List;

public interface IRelatorioDAO {
    List<RelatorioSemanalDTO> listarRelatorioSemanal();

    List<MovimentacaoDetalhesDTO> listarDetalhesPorSemana(LocalDate semanaInicio, LocalDate semanaFim);
}

