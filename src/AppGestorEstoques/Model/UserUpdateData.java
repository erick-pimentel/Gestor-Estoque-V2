package AppGestorEstoques.Model;

public record UserUpdateData(
        int id,
        String nome,
        String email,
        String senha,
        String perfil,
        boolean ativo
) {
}

