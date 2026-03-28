package AppGestorEstoques.Model;

public record UserProfile(
        int id,
        String nome,
        String email,
        String perfil,
        boolean ativo
) {
}

