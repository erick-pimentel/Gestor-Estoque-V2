package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.Produto;

public interface IProdutoDAO {
    void cadastrar(Produto produto);

    Produto buscarProduto(String searchTerm);

    Produto buscarPorCodigo(String codigo);

    void excluir(int idProduto);

    void atualizar(Produto produto);
}

