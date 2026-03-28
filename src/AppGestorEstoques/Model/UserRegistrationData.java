package AppGestorEstoques.Model;

public record UserRegistrationData(
        String nome,
        String email,
        String senha,
        String perfil,
        boolean ativo
) {
}

