package AppGestorEstoques.Service;

import AppGestorEstoques.DAO.FornecedorDAO;
import AppGestorEstoques.DAO.IFornecedorDAO;
import AppGestorEstoques.Model.Fornecedor;

import java.util.List;

public class FornecedorService {
    private final IFornecedorDAO fornecedorDAO;

    public FornecedorService() {
        this(new FornecedorDAO());
    }

    public FornecedorService(IFornecedorDAO fornecedorDAO) {
        this.fornecedorDAO = fornecedorDAO;
    }

    public void cadastrarFornecedor(String nome, String cnpj, String telefone, String email) {

        validarDados(nome,cnpj,telefone,email);

        Fornecedor fornecedor = new Fornecedor
                (0,
                        nome,
                        cnpj,
                        telefone,
                        email,
                        null
                );

        fornecedorDAO.cadastrar(fornecedor);
    }

    public void validarDados(String nome, String cnpj, String telefone, String email) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o nome do fornecedor.");
        }
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o cnpj do fornecedor.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o telefone do fornecedor.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Insira um email do valido para o fornecedor.");
        }
    }

    public Fornecedor buscarFornecedor(String searchInput) {
        if (searchInput == null || searchInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe nome, CNPJ ou ID do fornecedor");
        }
        return fornecedorDAO.buscarFornecedor(searchInput.trim());
    }


    public void excluirFornecedor(String searchInput) {
        Fornecedor fornecedor = buscarFornecedor(searchInput);
        if (fornecedor == null) {
            throw new IllegalArgumentException("Fornecedor '" + searchInput + "' nao encontrado");
        }
        fornecedorDAO.excluir(fornecedor);
    }

    public void editarFornecedor(String searchInput, String nome, String cnpj, String telefone, String email) {
        Fornecedor fornecedor = buscarFornecedor(searchInput);
        if (fornecedor == null) {
            throw new IllegalArgumentException("Fornecedor '" + searchInput + "' nao encontrado");
        }

        validarDados(nome,cnpj,telefone,email);

        Fornecedor novoFornecedor = new Fornecedor(
                fornecedor.idFornecedor(),
                nome,
                cnpj,
                telefone,
                email,
                fornecedor.dataCadastroFornecedor()
        );
        fornecedorDAO.atualizar(novoFornecedor);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorDAO.listarTodos();
    }
}
