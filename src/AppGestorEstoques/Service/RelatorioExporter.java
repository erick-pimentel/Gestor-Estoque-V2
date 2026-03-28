package AppGestorEstoques.Service;

import AppGestorEstoques.Model.MovimentacaoDetalhesDTO;
import AppGestorEstoques.Model.RelatorioSemanalDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface RelatorioExporter {
    void exportar(RelatorioSemanalDTO relatorio, List<MovimentacaoDetalhesDTO> detalhes, File destino) throws IOException;
}

