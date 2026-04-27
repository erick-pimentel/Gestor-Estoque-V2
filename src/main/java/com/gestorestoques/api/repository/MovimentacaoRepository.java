package com.gestorestoques.api.repository;

import com.gestorestoques.api.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Integer> {
    boolean existsByProdutoIdProduto(Integer idProduto);

    List<Movimentacao> findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(LocalDateTime dataInicio, LocalDateTime dataFim);
}

