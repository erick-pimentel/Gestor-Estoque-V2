package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.*;
import com.gestorestoques.api.model.*;
import com.gestorestoques.api.repository.MovimentacaoRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
public class RelatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);
    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    public RelatorioService(MovimentacaoRepository movimentacaoRepository,
                           ProdutoRepository produtoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    // ============ VALIDAÇÕES ============
    private void validarDatas(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataInicio e dataFim sao obrigatorias");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataFim nao pode ser anterior a dataInicio");
        }
        if (dataInicio.isAfter(LocalDate.now().plusDays(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataInicio nao pode ser uma data futura");
        }
    }

    private void validarProduto(Produto produto) {
        if (produto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado");
        }
    }

    // ============ RELATÓRIO SEMANAL (MOVIMENTAÇÕES) ============

    @Transactional(readOnly = true)
    public List<RelatorioSemanalDTO> gerarRelatorioSemanal(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Gerando relatorio semanal: {} a {}", dataInicio, dataFim);

        validarDatas(dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Movimentacao> movimentacoes = movimentacaoRepository
                .findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(inicio, fim);

        if (movimentacoes.isEmpty()) {
            logger.warn("Nenhuma movimentacao encontrada no periodo: {} a {}", dataInicio, dataFim);
            return new ArrayList<>();
        }

        logger.info("Total de movimentacoes encontradas: {}", movimentacoes.size());

        WeekFields weekFields = WeekFields.ISO;

        Map<String, Integer> totalEntradasSemana = new HashMap<>();
        Map<String, Integer> totalSaidasSemana = new HashMap<>();
        Map<String, Integer> totalMovimentacoesSemana = new HashMap<>();

        for (Movimentacao mov : movimentacoes) {
            String semana = calcularSemana(mov.getDataMovimentacao().toLocalDate(), weekFields);
            totalMovimentacoesSemana.merge(semana, 1, Integer::sum);

            if (mov.getTipoMovimentacao() == TipoMovimentacao.ENTRADA) {
                totalEntradasSemana.merge(semana, mov.getQuantidadeMovimentacao(), Integer::sum);
            }
            if (mov.getTipoMovimentacao() == TipoMovimentacao.SAIDA) {
                totalSaidasSemana.merge(semana, mov.getQuantidadeMovimentacao(), Integer::sum);
            }
        }

        Map<String, RelatorioSemanalDTO> agrupado = new LinkedHashMap<>();
        for (Movimentacao mov : movimentacoes) {
            validarProduto(mov.getProduto());

            String semana = calcularSemana(mov.getDataMovimentacao().toLocalDate(), weekFields);
            String chave = semana + "|" + mov.getProduto().getNomeProduto() + "|" + mov.getTipoMovimentacao();

            RelatorioSemanalDTO dto = agrupado.computeIfAbsent(chave, k -> {
                RelatorioSemanalDTO novo = new RelatorioSemanalDTO();
                novo.setSemana(semana);
                novo.setNomeProduto(mov.getProduto().getNomeProduto());
                novo.setTipo(mov.getTipoMovimentacao());
                novo.setQuantidade(0);
                novo.setValorTotal(BigDecimal.ZERO);
                novo.setTotalEntradas(totalEntradasSemana.getOrDefault(semana, 0));
                novo.setTotalSaidas(totalSaidasSemana.getOrDefault(semana, 0));
                novo.setTotalMovimentacoes(totalMovimentacoesSemana.getOrDefault(semana, 0));
                return novo;
            });

            dto.setQuantidade(dto.getQuantidade() + mov.getQuantidadeMovimentacao());
            BigDecimal unitario = mov.getValorUnitario() == null ? BigDecimal.ZERO : mov.getValorUnitario();
            logger.debug("Movimentacao ID: {}, Quantidade: {}, Valor Unitario: {}", mov.getIdMovimentacao(), mov.getQuantidadeMovimentacao(), unitario);
            BigDecimal valorTotalMov = unitario.multiply(BigDecimal.valueOf(mov.getQuantidadeMovimentacao()));
            dto.setValorTotal(dto.getValorTotal().add(valorTotalMov));
        }

        return new ArrayList<>(agrupado.values());
    }

    private String calcularSemana(LocalDate data, WeekFields weekFields) {
        int ano = data.get(weekFields.weekBasedYear());
        int semana = data.get(weekFields.weekOfWeekBasedYear());
        return String.format("%d-W%02d", ano, semana);
    }

    @Transactional(readOnly = true)
    public List<RelatorioEstoqueDTO> gerarRelatorioEstoque() {
        logger.info("Gerando relatorio de estoque completo");

        List<Produto> produtos = produtoRepository.findAll();

        if (produtos.isEmpty()) {
            logger.warn("Nenhum produto encontrado no estoque");
            return new ArrayList<>();
        }

        logger.info("Total de produtos: {}", produtos.size());

        List<RelatorioEstoqueDTO> relatorio = new ArrayList<>();

        for (Produto produto : produtos) {
            if (produto == null || produto.getIdProduto() == null) {
                logger.warn("Produto inválido encontrado, ignorando");
                continue;
            }

            BigDecimal precoVenda = produto.getPrecoVenda() == null ? BigDecimal.ZERO : produto.getPrecoVenda();
            Integer quantidade = produto.getQuantidade() == null ? 0 : produto.getQuantidade();

            RelatorioEstoqueDTO dto = new RelatorioEstoqueDTO(
                produto.getIdProduto(),
                produto.getNomeProduto(),
                quantidade,
                precoVenda,
                produto.getCodigoProduto()
            );
            relatorio.add(dto);
        }

        return relatorio;
    }

    @Transactional(readOnly = true)
    public List<RelatorioProdutoDTO> gerarRelatorioPorProduto(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Gerando relatorio por produto: {} a {}", dataInicio, dataFim);

        validarDatas(dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Movimentacao> movimentacoes = movimentacaoRepository
                .findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(inicio, fim);

        if (movimentacoes.isEmpty()) {
            logger.warn("Nenhuma movimentacao encontrada no periodo");
            return new ArrayList<>();
        }

        logger.info("Total de movimentacoes: {}", movimentacoes.size());

        Map<Integer, RelatorioProdutoDTO> relatorios = new LinkedHashMap<>();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getProduto() == null) {
                logger.warn("Movimentacao com produto nulo, ignorando");
                continue;
            }

            validarProduto(mov.getProduto());

            Integer idProduto = mov.getProduto().getIdProduto();
            relatorios.putIfAbsent(idProduto, new RelatorioProdutoDTO(
                idProduto,
                mov.getProduto().getNomeProduto(),
                0, 0, 0,
                mov.getProduto().getQuantidade(),
                BigDecimal.ZERO
            ));

            RelatorioProdutoDTO dto = relatorios.get(idProduto);
            if (mov.getTipoMovimentacao() == TipoMovimentacao.ENTRADA) {
                dto.setTotalEntradas(dto.getTotalEntradas() + mov.getQuantidadeMovimentacao());
            } else if (mov.getTipoMovimentacao() == TipoMovimentacao.SAIDA) {
                dto.setTotalSaidas(dto.getTotalSaidas() + mov.getQuantidadeMovimentacao());
            } else if (mov.getTipoMovimentacao() == TipoMovimentacao.AJUSTE) {
                dto.setTotalAjustes(dto.getTotalAjustes() + mov.getQuantidadeMovimentacao());
            }

            BigDecimal valorMov = mov.getValorUnitario() == null ? BigDecimal.ZERO : mov.getValorUnitario();
            valorMov = valorMov.multiply(BigDecimal.valueOf(mov.getQuantidadeMovimentacao()));
            dto.setValorMovimentado(dto.getValorMovimentado().add(valorMov));
            dto.setQuantidadeAtual(mov.getProduto().getQuantidade());
        }

        return new ArrayList<>(relatorios.values());
    }

    @Transactional(readOnly = true)
    public List<RelatorioPorFornecedorDTO> gerarRelatorioPorFornecedor(LocalDate dataInicio, LocalDate dataFim) {
        logger.info("Gerando relatorio por fornecedor: {} a {}", dataInicio, dataFim);

        validarDatas(dataInicio, dataFim);

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Movimentacao> movimentacoes = movimentacaoRepository
                .findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(inicio, fim);

        if (movimentacoes.isEmpty()) {
            logger.warn("Nenhuma movimentacao encontrada no periodo");
            return new ArrayList<>();
        }

        logger.info("Total de movimentacoes: {}", movimentacoes.size());

        Map<Integer, RelatorioPorFornecedorDTO> relatorios = new LinkedHashMap<>();
        Set<String> produtosPorFornecedor = new HashSet<>();

        for (Movimentacao mov : movimentacoes) {
            if (mov.getFornecedor() == null) {
                logger.debug("Movimentacao sem fornecedor, ignorando");
                continue;
            }

            if (mov.getProduto() == null) {
                logger.warn("Movimentacao com produto nulo, ignorando");
                continue;
            }

            Integer idFornecedor = mov.getFornecedor().getIdFornecedor();
            relatorios.putIfAbsent(idFornecedor, new RelatorioPorFornecedorDTO(
                idFornecedor,
                mov.getFornecedor().getNomeFornecedor(),
                0L, 0,
                BigDecimal.ZERO
            ));

            RelatorioPorFornecedorDTO dto = relatorios.get(idFornecedor);
            dto.setTotalMovimentacoes(dto.getTotalMovimentacoes() + 1);

            String chaveProduto = idFornecedor + "-" + mov.getProduto().getIdProduto();
            produtosPorFornecedor.add(chaveProduto);

            BigDecimal valorMov = mov.getValorUnitario() == null ? BigDecimal.ZERO : mov.getValorUnitario();
            valorMov = valorMov.multiply(BigDecimal.valueOf(mov.getQuantidadeMovimentacao()));
            dto.setValorTotalMovimentacoes(dto.getValorTotalMovimentacoes().add(valorMov));
        }

        for (RelatorioPorFornecedorDTO dto : relatorios.values()) {
            int totalProdutos = (int) produtosPorFornecedor.stream()
                    .filter(k -> k.startsWith(dto.getIdFornecedor() + "-"))
                    .count();
            dto.setTotalProdutos(totalProdutos);
        }

        return new ArrayList<>(relatorios.values());
    }
}
