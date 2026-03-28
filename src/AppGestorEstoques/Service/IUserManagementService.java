package AppGestorEstoques.Service;

import AppGestorEstoques.Model.UserProfile;
import AppGestorEstoques.Model.UserRegistrationData;
import AppGestorEstoques.Model.UserUpdateData;

import java.util.List;

public interface IUserManagementService {
    List<UserProfile> listarUsuarios();

    UserProfile buscarPorIdOuNome(String criterio);

    void cadastrarUsuario(UserRegistrationData data);

    void atualizarUsuario(UserUpdateData data);

    void excluirPorIdOuNome(String criterio);
}

