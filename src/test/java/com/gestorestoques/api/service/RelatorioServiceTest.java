package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.RelatorioEstoqueDTO;
import com.gestorestoques.api.dto.RelatorioPorFornecedorDTO;
import com.gestorestoques.api.dto.RelatorioProdutoDTO;
import com.gestorestoques.api.dto.RelatorioSemanalDTO;
import com.gestorestoques.api.model.*;
import com.gestorestoques.api.repository.MovimentacaoRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RelatorioService")
class RelatorioServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private Produto produto;
    private Usuario usuario;
    private Fornecedor fornecedor;
    private Movimentacao movimentacao;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNomeUsuario("admin");

        fornecedor = new Fornecedor();
        fornecedor.setIdFornecedor(1);
        fornecedor.setNomeFornecedor("Fornecedor A");

        produto = new Produto();
        produto.setIdProduto(1);
        produto.setNomeProduto("Produto Test");
        produto.setCodigoProduto("PROD001");
        produto.setQuantidade(100);
        produto.setPrecoCusto(BigDecimal.valueOf(10.00));
        produto.setPrecoVenda(BigDecimal.valueOf(15.00));
        produto.setFornecedor(fornecedor);

        movimentacao = new Movimentacao();
        movimentacao.setIdMovimentacao(1);
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidadeMovimentacao(50);
        movimentacao.setValorUnitario(BigDecimal.valueOf(10.00));
        movimentacao.setDataMovimentacao(LocalDateTime.now());
        movimentacao.setFornecedor(fornecedor);
    }

    @Test
    @DisplayName("Deve gerar relatório de estoque")
    void testGerarRelatorioEstoque() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<RelatorioEstoqueDTO> resultado = relatorioService.gerarRelatorioEstoque();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Test", resultado.get(0).getNomeProduto());
        assertEquals(100, resultado.get(0).getQuantidadeAtual());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver produtos")
    void testGerarRelatorioEstoqueVazio() {
        when(produtoRepository.findAll()).thenReturn(new ArrayList<>());

        List<RelatorioEstoqueDTO> resultado = relatorioService.gerarRelatorioEstoque();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("Deve gerar relatório semanal")
    void testGerarRelatorioSemanal() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(movimentacao));

        List<RelatorioSemanalDTO> resultado = relatorioService.gerarRelatorioSemanal(inicio, fim);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(movimentacaoRepository, times(1)).findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exception ao gerar relatório com data fim antes de data início")
    void testGerarRelatorioSemanalDataInvalida() {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = LocalDate.now().minusDays(1);

        assertThrows(ResponseStatusException.class,
            () -> relatorioService.gerarRelatorioSemanal(inicio, fim));
    }

    @Test
    @DisplayName("Deve lançar exception ao gerar relatório com data futura")
    void testGerarRelatorioSemanalDataFutura() {
        LocalDate inicio = LocalDate.now().plusDays(5);
        LocalDate fim = LocalDate.now().plusDays(10);

        assertThrows(ResponseStatusException.class,
            () -> relatorioService.gerarRelatorioSemanal(inicio, fim));
    }

    @Test
    @DisplayName("Deve gerar relatório por produto")
    void testGerarRelatorioPorProduto() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(movimentacao));

        List<RelatorioProdutoDTO> resultado = relatorioService.gerarRelatorioPorProduto(inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Test", resultado.get(0).getNomeProduto());
        verify(movimentacaoRepository, times(1)).findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(any(), any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver movimentações por produto")
    void testGerarRelatorioPorProdutoVazio() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(new ArrayList<>());

        List<RelatorioProdutoDTO> resultado = relatorioService.gerarRelatorioPorProduto(inicio, fim);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("Deve gerar relatório por fornecedor")
    void testGerarRelatorioPorFornecedor() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(movimentacao));

        List<RelatorioPorFornecedorDTO> resultado = relatorioService.gerarRelatorioPorFornecedor(inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Fornecedor A", resultado.get(0).getNomeFornecedor());
        verify(movimentacaoRepository, times(1)).findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(any(), any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver movimentações por fornecedor")
    void testGerarRelatorioPorFornecedorVazio() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(new ArrayList<>());

        List<RelatorioPorFornecedorDTO> resultado = relatorioService.gerarRelatorioPorFornecedor(inicio, fim);

        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    @Test
    @DisplayName("Deve contar movimentações por tipo no relatório semanal")
    void testGerarRelatorioSemanalComContadores() {
        LocalDate inicio = LocalDate.now().minusDays(7);
        LocalDate fim = LocalDate.now();

        Movimentacao entrada = new Movimentacao();
        entrada.setProduto(produto);
        entrada.setUsuario(usuario);
        entrada.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        entrada.setQuantidadeMovimentacao(50);
        entrada.setValorUnitario(BigDecimal.valueOf(10.00));
        entrada.setDataMovimentacao(LocalDateTime.now());

        when(movimentacaoRepository.findByDataMovimentacaoBetweenOrderByDataMovimentacaoAsc(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(entrada));

        List<RelatorioSemanalDTO> resultado = relatorioService.gerarRelatorioSemanal(inicio, fim);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getTotalEntradas() > 0);
    }

    @Test
    @DisplayName("Deve validar data nula no início")
    void testValidarDataNulaInicio() {
        assertThrows(ResponseStatusException.class,
            () -> relatorioService.gerarRelatorioSemanal(null, LocalDate.now()));
    }

    @Test
    @DisplayName("Deve validar data nula no fim")
    void testValidarDataNulaFim() {
        assertThrows(ResponseStatusException.class,
            () -> relatorioService.gerarRelatorioSemanal(LocalDate.now(), null));
    }

    @Test
    @DisplayName("Deve handle produtos com preço venda nulo")
    void testRelatorioComPrecoVendaNulo() {
        produto.setPrecoVenda(null);
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<RelatorioEstoqueDTO> resultado = relatorioService.gerarRelatorioEstoque();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        // Quando preço venda é nulo, retorna ZERO
        BigDecimal expected = resultado.get(0).getPrecoVenda();
        assertTrue(expected == null || expected.compareTo(BigDecimal.ZERO) == 0);
    }
}

