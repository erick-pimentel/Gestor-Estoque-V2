package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.RelatorioSemanalDTO;
import com.gestorestoques.api.model.Movimentacao;
import com.gestorestoques.api.model.TipoMovimentacao;
import com.gestorestoques.api.repository.MovimentacaoRepository;
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

    private final MovimentacaoRepository movimentacaoRepository;

    public RelatorioService(MovimentacaoRepository movimentacaoRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<RelatorioSemanalDTO> gerarRelatorioSemanal(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataInicio e dataFim sao obrigatorias");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataFim nao pode ser anterior a dataInicio");
        }

        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        List<Movimentacao> movimentacoes = movimentacaoRepository
                .findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(inicio, fim);

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
}

