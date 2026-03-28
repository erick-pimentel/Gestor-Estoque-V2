package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.Movimentacao;

import java.util.List;

public interface IMovimentacaoDAO {
    void registrarMovimentacao(Movimentacao movimentacao);

    List<Movimentacao> listarMovimentacoes();

    int excluirUltimaMovimentacaoPorUsuario(int idUsuario);
}

