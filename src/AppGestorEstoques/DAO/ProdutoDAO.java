package AppGestorEstoques.DAO;

import AppGestorEstoques.Infra.Conexao;
import AppGestorEstoques.Model.Produto;

import java.sql.*;

public class ProdutoDAO implements IProdutoDAO {
    public void cadastrar(Produto produto) {
        String sql= """
                INSERT INTO produtos(id_usuario,id_fornecedor,nome_produto,codigo_produto,
                quantidade_estoque,valor_compra,valor_venda,validade_produto) VALUES (?,?,?,?,?,?,?,?);
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.idUsuario());
            stmt.setInt(2, produto.idFornecedor());
            stmt.setString(3, produto.nomeProduto());
            stmt.setString(4, produto.codigoProduto());
            stmt.setInt(5, produto.quantidadeEstoque());
            stmt.setBigDecimal(6, produto.valorCompra());
            stmt.setBigDecimal(7, produto.valorVenda());
            stmt.setDate(8, produto.validadeProduto());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar: " + e.getMessage(), e);
        }
    }

    public Produto buscarProduto(String searchTerm) {
        // Try searching by ID first (if it's a number)
        try {
            int id = Integer.parseInt(searchTerm);
            String sql = """
                    SELECT id_produto, id_usuario, id_fornecedor, nome_produto, codigo_produto,
                           quantidade_estoque, valor_compra, valor_venda, validade_produto, data_cadastro
                    FROM produtos WHERE id_produto = ?
                    """;
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return construirProduto(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
            }
        } catch (NumberFormatException e) {
            // Not a number, continue to search by code/name
        }

        // Search by code
        String sqlCodigo = """
                SELECT id_produto, id_usuario, id_fornecedor, nome_produto, codigo_produto,
                       quantidade_estoque, valor_compra, valor_venda, validade_produto, data_cadastro
                FROM produtos WHERE codigo_produto = ?
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCodigo)) {
            stmt.setString(1, searchTerm);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirProduto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }

        // Search by name
        String sqlNome = """
                SELECT id_produto, id_usuario, id_fornecedor, nome_produto, codigo_produto,
                       quantidade_estoque, valor_compra, valor_venda, validade_produto, data_cadastro
                FROM produtos WHERE nome_produto LIKE ?
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlNome)) {
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return construirProduto(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }

        return null;
    }

    // Helper method to construct Produto from ResultSet
    private Produto construirProduto(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id_produto"),
                rs.getInt("id_usuario"),
                rs.getInt("id_fornecedor"),
                rs.getString("nome_produto"),
                rs.getString("codigo_produto"),
                rs.getInt("quantidade_estoque"),
                rs.getBigDecimal("valor_compra"),
                rs.getBigDecimal("valor_venda"),
                rs.getDate("validade_produto"),
                rs.getDate("data_cadastro")
        );
    }

    // Keep old method for backward compatibility
    public Produto buscarPorCodigo(String codigo) {
        return buscarProduto(codigo);
    }


    public void excluir(int idProduto) {
        String sql = "DELETE FROM produtos WHERE id_produto=?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Produto não encontrado para exclusão");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir: " + e.getMessage(), e);
        }
    }

    public void atualizar(Produto produto) {
        String sql = """
                UPDATE produtos SET id_usuario=?, id_fornecedor=?, nome_produto=?, codigo_produto=?,
                quantidade_estoque=?, valor_compra=?, valor_venda=?, validade_produto=?
                WHERE id_produto=?;
                """;
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produto.idUsuario());
            stmt.setInt(2, produto.idFornecedor());
            stmt.setString(3, produto.nomeProduto());
            stmt.setString(4, produto.codigoProduto());
            stmt.setInt(5, produto.quantidadeEstoque());
            stmt.setBigDecimal(6, produto.valorCompra());
            stmt.setBigDecimal(7, produto.valorVenda());
            stmt.setDate(8, produto.validadeProduto());
            stmt.setInt(9, produto.idProduto());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Produto não encontrado para atualização");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar: " + e.getMessage(), e);
        }
    }
}
