package com.gestorestoques.api.repository;

import com.gestorestoques.api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    boolean existsByCodigoProduto(String codigoProduto);

    boolean existsByCodigoProdutoAndIdProdutoNot(String codigoProduto, Integer idProduto);

    boolean existsByFornecedorIdFornecedor(Integer idFornecedor);
}

