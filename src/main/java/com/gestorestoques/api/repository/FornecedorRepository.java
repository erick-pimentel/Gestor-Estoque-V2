package com.gestorestoques.api.repository;

import com.gestorestoques.api.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {
    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdFornecedorNot(String cnpj, Integer idFornecedor);
}

