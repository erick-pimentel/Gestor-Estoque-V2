/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AppGestorEstoques.DAO;

import AppGestorEstoques.Infra.Conexao;
import AppGestorEstoques.Model.User;
import AppGestorEstoques.Model.UserProfile;
import AppGestorEstoques.Model.UserRegistrationData;
import AppGestorEstoques.Model.UserUpdateData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Ericz
 */
public class UserDAO implements IUserDAO, IUserManagementDAO {
    private final Conexao conexao;

    public UserDAO(Conexao conexao) {
        this.conexao = Objects.requireNonNull(conexao, "conexao nao pode ser nula");
    }

    @Override
    public User buscarLogin(String userName, String userPass) throws SQLException {
        String sql = "SELECT id_usuario,nome_usuario,perfil_usuario,usuario_ativo FROM usuarios " +
                "WHERE nome_usuario = ? AND senha_usuario = ?;";

        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userName);
            stmt.setString(2, userPass);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id_usuario");
                String nome = rs.getString("nome_usuario");
                String perfil = rs.getString("perfil_usuario");
                boolean ativo = rs.getBoolean("usuario_ativo");

                return new User(id, nome, perfil, ativo);
            } else {
                return null;
            }
        }
    }

    @Override
    public List<UserProfile> listarUsuarios() {
        String sql = "SELECT id_usuario, nome_usuario, email_usuario, perfil_usuario, usuario_ativo FROM usuarios ORDER BY nome_usuario";
        List<UserProfile> usuarios = new ArrayList<>();

        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new UserProfile(
                        rs.getInt("id_usuario"),
                        rs.getString("nome_usuario"),
                        rs.getString("email_usuario"),
                        rs.getString("perfil_usuario"),
                        rs.getBoolean("usuario_ativo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuarios: " + e.getMessage(), e);
        }

        return usuarios;
    }

    @Override
    public UserProfile buscarPorIdOuNome(String criterio) {
        String termo = criterio == null ? "" : criterio.trim();
        if (termo.isEmpty()) {
            return null;
        }

        try {
            int id = Integer.parseInt(termo);
            String sqlById = "SELECT id_usuario, nome_usuario, email_usuario, perfil_usuario, usuario_ativo FROM usuarios WHERE id_usuario = ?";
            try (Connection conn = conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlById)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return toProfile(rs);
                    }
                }
            }
        } catch (NumberFormatException ignored) {
            // criterio nao numerico, segue busca por nome
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por ID: " + e.getMessage(), e);
        }

        String sqlByName = "SELECT id_usuario, nome_usuario, email_usuario, perfil_usuario, usuario_ativo FROM usuarios WHERE LOWER(nome_usuario) = LOWER(?)";
        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlByName)) {
            stmt.setString(1, termo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return toProfile(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuario por nome: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void cadastrarUsuario(UserRegistrationData data) {
        String sql = "INSERT INTO usuarios(nome_usuario, email_usuario, senha_usuario, perfil_usuario, usuario_ativo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, data.nome());
            stmt.setString(2, data.email());
            stmt.setString(3, data.senha());
            stmt.setString(4, data.perfil());
            stmt.setBoolean(5, data.ativo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void atualizarUsuario(UserUpdateData data) {
        boolean atualizaSenha = data.senha() != null && !data.senha().isBlank();
        String sql = atualizaSenha
                ? "UPDATE usuarios SET nome_usuario = ?, email_usuario = ?, senha_usuario = ?, perfil_usuario = ?, usuario_ativo = ? WHERE id_usuario = ?"
                : "UPDATE usuarios SET nome_usuario = ?, email_usuario = ?, perfil_usuario = ?, usuario_ativo = ? WHERE id_usuario = ?";

        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data.nome());
            if (atualizaSenha) {
                stmt.setString(2, data.email());
                stmt.setString(3, data.senha());
                stmt.setString(4, data.perfil());
                stmt.setBoolean(5, data.ativo());
                stmt.setInt(6, data.id());
            } else {
                stmt.setString(2, data.email());
                stmt.setString(3, data.perfil());
                stmt.setBoolean(4, data.ativo());
                stmt.setInt(5, data.id());
            }

            int linhas = stmt.executeUpdate();
            if (linhas == 0) {
                throw new RuntimeException("Usuario nao encontrado para atualizacao.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluirPorIdOuNome(String criterio) {
        UserProfile usuario = buscarPorIdOuNome(criterio);
        if (usuario == null) {
            throw new RuntimeException("Usuario nao encontrado para exclusao.");
        }

        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuario.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir usuario: " + e.getMessage(), e);
        }
    }

    private UserProfile toProfile(ResultSet rs) throws SQLException {
        return new UserProfile(
                rs.getInt("id_usuario"),
                rs.getString("nome_usuario"),
                rs.getString("email_usuario"),
                rs.getString("perfil_usuario"),
                rs.getBoolean("usuario_ativo")
        );
    }
}

