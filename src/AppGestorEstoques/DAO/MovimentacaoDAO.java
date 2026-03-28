package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.Movimentacao;
import AppGestorEstoques.Infra.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MovimentacaoDAO implements IMovimentacaoDAO {
    public void registrarMovimentacao(Movimentacao movimentacao) {
        String sql = """
                INSERT INTO movimentacoes (id_usuario, quantidade_movimentacao, tipo_movimentacao)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movimentacao.idUser());
            stmt.setString(3, movimentacao.tipoMovimentacao().name());
            stmt.setInt(2, movimentacao.qtdMovimentacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar movimentação: " + e.getMessage(), e);
        }
    }

    public List<Movimentacao> listarMovimentacoes() {
        String sql = "SELECT id_movimentacao, id_usuario, quantidade_movimentacao, tipo_movimentacao, data_movimentacao FROM movimentacoes";
        List<Movimentacao> movimentacoes = new ArrayList<>();

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idMovimentacao = rs.getInt("id_movimentacao");
                int idUser = rs.getInt("id_usuario");
                int qtdMovimentacao = rs.getInt("quantidade_movimentacao");
                Movimentacao.TipoMovimentacao tipoMovimentacao = Movimentacao.TipoMovimentacao.fromText(rs.getString("tipo_movimentacao"));
                Timestamp dataMovimentacao = rs.getTimestamp("data_movimentacao");

                Movimentacao movimentacao = new Movimentacao(idMovimentacao, idUser, qtdMovimentacao, tipoMovimentacao, dataMovimentacao);
                movimentacoes.add(movimentacao);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações: " + e.getMessage(), e);
        }

        return movimentacoes;
    }

    public int excluirUltimaMovimentacaoPorUsuario(int idUsuario) {
        String sql = """
                DELETE FROM movimentacoes
                WHERE id_movimentacao = (
                    SELECT id_movimentacao FROM (
                        SELECT id_movimentacao
                        FROM movimentacoes
                        WHERE id_usuario = ?
                        ORDER BY id_movimentacao DESC
                        LIMIT 1
                    ) AS ultima_movimentacao
                )
                """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar ultima movimentacao: " + e.getMessage(), e);
        }
    }
}

