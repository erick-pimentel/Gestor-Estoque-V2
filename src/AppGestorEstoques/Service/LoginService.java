/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package AppGestorEstoques.Service;

import AppGestorEstoques.DAO.UserDAO;
import AppGestorEstoques.DAO.IUserDAO;
import AppGestorEstoques.Infra.Conexao;
import AppGestorEstoques.Model.User;

import java.sql.SQLException;
import java.util.Objects;

/**
 *
 * @author Ericz
 */
public class LoginService implements ILoginService {
    private final IUserDAO userDAO;

    public LoginService(Conexao conexao) {
        this(new UserDAO(conexao));
    }

    public LoginService(IUserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO nao pode ser nulo");
    }

    @Override
    public User autenticar(String nomeUsuario, String senha) throws SQLException {
        String usuario = nomeUsuario == null ? "" : nomeUsuario.trim();
        String senhaUsuario = senha == null ? "" : senha;

        if (usuario.isEmpty() || senhaUsuario.isEmpty()) {
            return null;
        }

        User usuarioAutenticado = userDAO.buscarLogin(usuario, senhaUsuario);
        if (usuarioAutenticado != null && !usuarioAutenticado.isAtivo()) {
            throw new IllegalStateException("Usuario inativo.");
        }

        return usuarioAutenticado;
    }
}
