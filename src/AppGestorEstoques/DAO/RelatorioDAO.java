package AppGestorEstoques.DAO;

import AppGestorEstoques.Infra.Conexao;
import AppGestorEstoques.Model.RelatorioSemanalDTO;
import AppGestorEstoques.Model.MovimentacaoDetalhesDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO implements IRelatorioDAO {
    public List<RelatorioSemanalDTO> listarRelatorioSemanal(){
        List<RelatorioSemanalDTO> relatorios = new ArrayList<>();

        // Aggregate first by ISO week, then compute week boundaries from the aggregated date.
        String sql = """
        SELECT
            DATE_SUB(base.data_base, INTERVAL WEEKDAY(base.data_base) DAY) AS periodo_inicio,
            DATE_ADD(DATE_SUB(base.data_base, INTERVAL WEEKDAY(base.data_base) DAY), INTERVAL 6 DAY) AS periodo_fim,
            base.total_produtos,
            base.produtos_vendidos,
            base.faturamento
        FROM (
            SELECT
                YEARWEEK(m.data_movimentacao, 1) AS ano_semana,
                MIN(DATE(m.data_movimentacao)) AS data_base,
                COUNT(DISTINCT m.id_usuario) AS total_produtos,
                SUM(CASE WHEN m.tipo_movimentacao IN ('SAIDA', 'AJUSTE') THEN m.quantidade_movimentacao ELSE 0 END) AS produtos_vendidos,
                CAST(0 AS DECIMAL(10,2)) AS faturamento
            FROM movimentacoes m
            GROUP BY YEARWEEK(m.data_movimentacao, 1)
        ) base
        ORDER BY periodo_inicio DESC
        LIMIT 12
        """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate inicio = rs.getDate("periodo_inicio").toLocalDate();
                LocalDate fim = rs.getDate("periodo_fim").toLocalDate();
                relatorios.add(new RelatorioSemanalDTO(
                        inicio, fim,
                        rs.getInt("total_produtos"),
                        rs.getInt("produtos_vendidos"),
                        rs.getBigDecimal("faturamento"),
                        new ArrayList<>()  // detalhes carregados depois
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro relatórios semanais: " + e.getMessage(), e);
        }
        return relatorios;
    }

    public List<MovimentacaoDetalhesDTO> listarDetalhesPorSemana(LocalDate semanaInicio, LocalDate semanaFim) {
        List<MovimentacaoDetalhesDTO> detalhes = new ArrayList<>();

        String sql = """
            SELECT m.id_movimentacao, p.nome_produto, m.tipo_movimentacao,
                   m.quantidade_movimentacao, m.data_movimentacao
            FROM movimentacoes m
            JOIN produtos p ON m.id_usuario = p.id_usuario
            WHERE m.data_movimentacao >= ? AND m.data_movimentacao < ?
            ORDER BY m.data_movimentacao DESC
        """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(semanaInicio.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(semanaFim.plusDays(1).atStartOfDay()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalhes.add(new MovimentacaoDetalhesDTO(
                            rs.getInt("id_movimentacao"),
                            rs.getInt("quantidade_movimentacao"),
                            rs.getString("nome_produto"),
                            rs.getString("tipo_movimentacao"),
                            rs.getTimestamp("data_movimentacao").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro detalhes semana", e);
        }
        return detalhes;
    }
}
