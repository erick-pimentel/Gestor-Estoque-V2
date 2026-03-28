package AppGestorEstoques.Service;

import AppGestorEstoques.Model.User;

import java.sql.SQLException;

public interface ILoginService {
    User autenticar(String nomeUsuario, String senha) throws SQLException;
}

