package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.User;

import java.sql.SQLException;

public interface IUserDAO {
    User buscarLogin(String userName, String userPass) throws SQLException;
}

