package AppGestorEstoques.Model;

import java.sql.Date;

public record Fornecedor(
        int idFornecedor,
        String nomeFornecedor,
        String cnpjFornecedor,
        String telefoneFornecedor,
        String emailFornecedor,
        Date dataCadastroFornecedor
    )
{
    public Fornecedor {
        if (idFornecedor < 0) {
            throw new IllegalArgumentException("Insira um ID valido");
        }
        if (nomeFornecedor == null || nomeFornecedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o nome do fornecedor.");
        }
        if (cnpjFornecedor == null || cnpjFornecedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o cnpj do fornecedor.");
        }
        if (telefoneFornecedor == null || telefoneFornecedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o telefone do fornecedor.");
        }
        if (emailFornecedor == null || emailFornecedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Insira um email do valido para o fornecedor.");
        }
    }
}

