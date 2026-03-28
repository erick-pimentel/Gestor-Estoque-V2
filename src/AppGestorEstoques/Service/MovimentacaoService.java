package AppGestorEstoques.Service;

import AppGestorEstoques.DAO.MovimentacaoDAO;
import AppGestorEstoques.DAO.IMovimentacaoDAO;
import AppGestorEstoques.Model.Movimentacao;
import AppGestorEstoques.Model.User;

import java.util.List;


public class MovimentacaoService {
    private final IMovimentacaoDAO movimentacaoDAO;

    public MovimentacaoService() {
        this(new MovimentacaoDAO());
    }

    public MovimentacaoService(IMovimentacaoDAO movimentacaoDAO) {
        this.movimentacaoDAO = movimentacaoDAO;
    }

    public void registrarMovimentacao(User user, int qtdMovimentacao, Movimentacao.TipoMovimentacao tipoMovimentacao) {
        if (user == null) throw new IllegalArgumentException("Informe o usuario.");
        if (qtdMovimentacao <= 0) throw new IllegalArgumentException("Preencha com a quantidade.");
        if (tipoMovimentacao == null) throw new IllegalArgumentException("Preencha o tipo de movimentacao.");

        Movimentacao movimentacao = new Movimentacao(0, user.getId(), qtdMovimentacao, tipoMovimentacao, null);
        movimentacaoDAO.registrarMovimentacao(movimentacao);
    }

    public void registrarMovimentacao(User user, String quantidadeTexto, String tipoMovimentacaoTexto) {
        int quantidade;
        try {
            quantidade = Integer.parseInt(quantidadeTexto == null ? "" : quantidadeTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantidade invalida. Digite um numero inteiro.", e);
        }

        Movimentacao.TipoMovimentacao tipo = Movimentacao.TipoMovimentacao.fromText(tipoMovimentacaoTexto);
        registrarMovimentacao(user, quantidade, tipo);
    }

    public List<Movimentacao> getMovimentacoes() {
        return movimentacaoDAO.listarMovimentacoes();
    }

    public void cancelarUltimaMovimentacao(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Informe o usuario.");
        }

        int linhasAfetadas = movimentacaoDAO.excluirUltimaMovimentacaoPorUsuario(user.getId());
        if (linhasAfetadas == 0) {
            throw new IllegalArgumentException("Nenhuma movimentacao encontrada para cancelar.");
        }
    }
}
