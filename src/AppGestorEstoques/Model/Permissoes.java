package AppGestorEstoques.Model;

public class Permissoes {
    public enum permissao {
        CadastroProdutos("Administrador","Operador de Estoque"),
        CadastroFornecedor("Administrador","Operador de Estoque"),
        Movimentacoes("Administrador","Gestor"),
        Relatorios("Administrador","Gestor"),
        Gestao("Administrador"),
        Sair("");

        private final String[] perfisPermitidos;

        permissao(String... perfis){
            this.perfisPermitidos = perfis;
        }

        public boolean temPermissao(String perfilUser){
            if (perfilUser==null) return false;

            for (String permitido : perfisPermitidos) {
                if (permitido.equalsIgnoreCase(perfilUser)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean temPermissao(String perfilUser, permissao permissao){
        return permissao.temPermissao(perfilUser);
    }
}
