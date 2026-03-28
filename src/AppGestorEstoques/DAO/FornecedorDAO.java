package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.Fornecedor;
import AppGestorEstoques.Infra.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FornecedorDAO implements IFornecedorDAO {
    public void cadastrar(Fornecedor fornecedor) {
        String sql = """
                INSERT INTO fornecedores (nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor)
                VALUES (?, ?, ?, ?);
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fornecedor.nomeFornecedor());
            stmt.setString(2, fornecedor.cnpjFornecedor());
            stmt.setString(3, fornecedor.telefoneFornecedor());
            stmt.setString(4, fornecedor.emailFornecedor());

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao cadastrar fornecedor: " + e.getMessage(), e);
        }
    }

    public List<Fornecedor> listarTodos() {
        List<Fornecedor> lista = new ArrayList<>();
        String sql = "SELECT id_fornecedor, nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor FROM fornecedores ORDER BY nome_fornecedor";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Fornecedor(
                        rs.getInt("id_fornecedor"),
                        rs.getString("nome_fornecedor"),
                        rs.getString("cnpj_fornecedor"),
                        rs.getString("contato_fornecedor"),
                        rs.getString("email_fornecedor"),
                        null
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar fornecedores: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizar(Fornecedor fornecedor) {
        String sql = """
                UPDATE fornecedores SET nome_fornecedor = ?, cnpj_fornecedor = ?, contato_fornecedor = ?, email_fornecedor = ?
                WHERE id_fornecedor = ?;
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fornecedor.nomeFornecedor());
            stmt.setString(2, fornecedor.cnpjFornecedor());
            stmt.setString(3, fornecedor.telefoneFornecedor());
            stmt.setString(4, fornecedor.emailFornecedor());
            stmt.setInt(5, fornecedor.idFornecedor());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        }
    }

    public void excluir(Fornecedor fornecedor) {
        String sql = """
                DELETE FROM fornecedores WHERE id_fornecedor = ?;
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fornecedor.idFornecedor());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir fornecedor: " + e.getMessage(), e);
        }
    }

    public Fornecedor buscarFornecedor(String searchTerm) {
        String termo = searchTerm == null ? "" : searchTerm.trim();
        if (termo.isEmpty()) {
            return null;
        }

        try {
            int id = Integer.parseInt(termo);

            String sqlId = """
                SELECT id_fornecedor, nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor
                FROM fornecedores WHERE id_fornecedor = ?;
            """;

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlId)) {

                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return contruirFornecedor(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar fornecedor por ID: " + e.getMessage(), e);
            }
        } catch (NumberFormatException e) {
            // Nao e um numero, seguir para outras buscas.
        }

        String cnpjApenasNumeros = termo.replaceAll("\\D", "");
        String sqlCNPJ = """
                SELECT id_fornecedor, nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor
                FROM fornecedores
                WHERE cnpj_fornecedor = ?
                   OR REPLACE(REPLACE(REPLACE(cnpj_fornecedor, '.', ''), '/', ''), '-', '') = ?;
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCNPJ)) {

            stmt.setString(1, termo);
            stmt.setString(2, cnpjApenasNumeros);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return contruirFornecedor(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedor por CNPJ: " + e.getMessage(), e);
        }

        String sqlNome = """
                SELECT id_fornecedor, nome_fornecedor, cnpj_fornecedor, contato_fornecedor, email_fornecedor
                FROM fornecedores WHERE LOWER(nome_fornecedor) LIKE LOWER(?);
            """;

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlNome)) {

            stmt.setString(1, "%" + termo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return contruirFornecedor(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar fornecedor por nome: " + e.getMessage(), e);
        }
        return null;
    }

    private Fornecedor contruirFornecedor(ResultSet rs) throws SQLException {
        return new Fornecedor(
                rs.getInt("id_fornecedor"),
                rs.getString("nome_fornecedor"),
                rs.getString("cnpj_fornecedor"),
                rs.getString("contato_fornecedor"),
                rs.getString("email_fornecedor"),
                null
        );
    }

}
