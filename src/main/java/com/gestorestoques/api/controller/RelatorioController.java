package com.gestorestoques.api.controller;

import com.gestorestoques.api.dto.*;
import com.gestorestoques.api.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/movimentacoes")
    public ResponseEntity<List<RelatorioSemanalDTO>> relatorioMovimentacoes(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioSemanal(dataInicio, dataFim));
    }

    @GetMapping("/estoque")
    public ResponseEntity<List<RelatorioEstoqueDTO>> relatorioEstoque() {
        return ResponseEntity.ok(relatorioService.gerarRelatorioEstoque());
    }

    @GetMapping("/produtos")
    public ResponseEntity<List<RelatorioProdutoDTO>> relatorioProdutos(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioPorProduto(dataInicio, dataFim));
    }

    @GetMapping("/fornecedores")
    public ResponseEntity<List<RelatorioPorFornecedorDTO>> relatorioFornecedores(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return ResponseEntity.ok(relatorioService.gerarRelatorioPorFornecedor(dataInicio, dataFim));
    }
}

