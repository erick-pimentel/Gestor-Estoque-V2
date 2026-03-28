package AppGestorEstoques.Service;

import AppGestorEstoques.DAO.IUserManagementDAO;
import AppGestorEstoques.DAO.UserDAO;
import AppGestorEstoques.Infra.Conexao;
import AppGestorEstoques.Model.UserProfile;
import AppGestorEstoques.Model.UserRegistrationData;
import AppGestorEstoques.Model.UserUpdateData;

import java.util.List;
import java.util.Objects;

public class UserManagementService implements IUserManagementService {
    private final IUserManagementDAO userManagementDAO;

    public UserManagementService() {
        this(new UserDAO(new Conexao()));
    }

    public UserManagementService(IUserManagementDAO userManagementDAO) {
        this.userManagementDAO = Objects.requireNonNull(userManagementDAO, "userManagementDAO nao pode ser nulo");
    }

    @Override
    public List<UserProfile> listarUsuarios() {
        return userManagementDAO.listarUsuarios();
    }

    @Override
    public UserProfile buscarPorIdOuNome(String criterio) {
        validarCriterio(criterio);
        return userManagementDAO.buscarPorIdOuNome(criterio.trim());
    }

    @Override
    public void cadastrarUsuario(UserRegistrationData data) {
        validarCadastro(data);
        userManagementDAO.cadastrarUsuario(new UserRegistrationData(
                data.nome().trim(),
                data.email().trim(),
                data.senha(),
                data.perfil().trim(),
                data.ativo()
        ));
    }

    @Override
    public void atualizarUsuario(UserUpdateData data) {
        validarAtualizacao(data);
        String senha = data.senha() == null ? null : data.senha().trim();
        if (senha != null && senha.isEmpty()) {
            senha = null; // senha em branco no edit significa manter a senha atual.
        }

        userManagementDAO.atualizarUsuario(new UserUpdateData(
                data.id(),
                data.nome().trim(),
                data.email().trim(),
                senha,
                data.perfil().trim(),
                data.ativo()
        ));
    }

    @Override
    public void excluirPorIdOuNome(String criterio) {
        validarCriterio(criterio);
        userManagementDAO.excluirPorIdOuNome(criterio.trim());
    }

    private void validarCadastro(UserRegistrationData data) {
        if (data == null) {
            throw new IllegalArgumentException("Dados de cadastro nao informados.");
        }
        validarNome(data.nome());
        validarEmail(data.email());
        validarPerfil(data.perfil());
        validarSenhaObrigatoria(data.senha());
    }

    private void validarAtualizacao(UserUpdateData data) {
        if (data == null) {
            throw new IllegalArgumentException("Dados de atualizacao nao informados.");
        }
        if (data.id() <= 0) {
            throw new IllegalArgumentException("ID de usuario invalido.");
        }
        validarNome(data.nome());
        validarEmail(data.email());
        validarPerfil(data.perfil());
        if (data.senha() != null && !data.senha().isBlank() && data.senha().trim().length() < 3) {
            throw new IllegalArgumentException("Senha deve ter ao menos 3 caracteres.");
        }
    }

    private void validarCriterio(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o ID ou nome do usuario.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome de usuario obrigatorio.");
        }
    }

    private void validarPerfil(String perfil) {
        if (perfil == null || perfil.trim().isEmpty()) {
            throw new IllegalArgumentException("Perfil de usuario obrigatorio.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email de usuario obrigatorio.");
        }
        String emailTrim = email.trim();
        if (!emailTrim.contains("@") || !emailTrim.contains(".")) {
            throw new IllegalArgumentException("Email de usuario invalido.");
        }
    }

    private void validarSenhaObrigatoria(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha obrigatoria.");
        }
        if (senha.trim().length() < 3) {
            throw new IllegalArgumentException("Senha deve ter ao menos 3 caracteres.");
        }
    }
}

