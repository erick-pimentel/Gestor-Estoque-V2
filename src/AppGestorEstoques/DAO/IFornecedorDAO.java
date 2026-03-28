package AppGestorEstoques.DAO;

import AppGestorEstoques.Model.Fornecedor;

import java.util.List;

public interface IFornecedorDAO {
    void cadastrar(Fornecedor fornecedor);

    List<Fornecedor> listarTodos();

    void atualizar(Fornecedor fornecedor);

    void excluir(Fornecedor fornecedor);

    Fornecedor buscarFornecedor(String searchTerm);
}

