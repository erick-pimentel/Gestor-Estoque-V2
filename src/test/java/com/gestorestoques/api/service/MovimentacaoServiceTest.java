package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.MovimentacaoDTO;
import com.gestorestoques.api.dto.MovimentacaoResponseDTO;
import com.gestorestoques.api.model.*;
import com.gestorestoques.api.repository.FornecedorRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MovimentacaoService")
class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    private MovimentacaoDTO movimentacaoDTO;
    private Movimentacao movimentacao;
    private Produto produto;
    private Usuario usuario;
    private Fornecedor fornecedor;

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

        movimentacao = new Movimentacao();
        movimentacao.setIdMovimentacao(1);
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidadeMovimentacao(50);
        movimentacao.setValorUnitario(BigDecimal.valueOf(10.00));
        movimentacao.setDataMovimentacao(LocalDateTime.now());

        movimentacaoDTO = new MovimentacaoDTO();
        movimentacaoDTO.setIdProduto(1);
        movimentacaoDTO.setTipo(TipoMovimentacao.ENTRADA);
        movimentacaoDTO.setQuantidade(50);
        movimentacaoDTO.setValorUnitario(BigDecimal.valueOf(10.00));
    }

    @Test
    @DisplayName("Deve listar todas as movimentações")
    void testListarTodos() {
        when(movimentacaoRepository.findAll()).thenReturn(List.of(movimentacao));

        List<MovimentacaoResponseDTO> resultado = movimentacaoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(movimentacaoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar movimentação por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(movimentacaoRepository.findById(1)).thenReturn(Optional.of(movimentacao));

        MovimentacaoResponseDTO resultado = movimentacaoService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        verify(movimentacaoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exception ao buscar movimentação inexistente")
    void testBuscarPorIdNotFound() {
        when(movimentacaoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> movimentacaoService.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve registrar entrada de estoque com sucesso")
    void testCriarEntradaSucesso() {
        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.save(any())).thenReturn(movimentacao);
        when(produtoRepository.save(any())).thenReturn(produto);

        MovimentacaoResponseDTO resultado = movimentacaoService.criar(movimentacaoDTO);

        assertNotNull(resultado);
        assertEquals(150, produto.getQuantidade()); // 100 + 50
        verify(movimentacaoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve registrar saída de estoque com sucesso")
    void testCriarSaidaSucesso() {
        movimentacaoDTO.setTipo(TipoMovimentacao.SAIDA);
        produto.setQuantidade(100);

        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.save(any())).thenReturn(movimentacao);
        when(produtoRepository.save(any())).thenReturn(produto);

        MovimentacaoResponseDTO resultado = movimentacaoService.criar(movimentacaoDTO);

        assertNotNull(resultado);
        assertEquals(50, produto.getQuantidade()); // 100 - 50
        verify(movimentacaoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer saída com estoque insuficiente")
    void testCriarSaidaComEstoqueInsuficiente() {
        movimentacaoDTO.setTipo(TipoMovimentacao.SAIDA);
        movimentacaoDTO.setQuantidade(150);
        produto.setQuantidade(100);

        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        assertThrows(ResponseStatusException.class, () -> movimentacaoService.criar(movimentacaoDTO));
    }

    @Test
    @DisplayName("Deve registrar ajuste de estoque com sucesso")
    void testCriarAjusteSucesso() {
        movimentacaoDTO.setTipo(TipoMovimentacao.AJUSTE);
        movimentacaoDTO.setQuantidade(80);

        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.save(any())).thenReturn(movimentacao);
        when(produtoRepository.save(any())).thenReturn(produto);

        MovimentacaoResponseDTO resultado = movimentacaoService.criar(movimentacaoDTO);

        assertNotNull(resultado);
        assertEquals(80, produto.getQuantidade());
        verify(movimentacaoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exception ao criar movimentação sem quantidade")
    void testCriarSemQuantidade() {
        movimentacaoDTO.setQuantidade(null);

        assertThrows(ResponseStatusException.class, () -> movimentacaoService.criar(movimentacaoDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar movimentação com quantidade zero")
    void testCriarComQuantidadeZero() {
        movimentacaoDTO.setQuantidade(0);

        assertThrows(ResponseStatusException.class, () -> movimentacaoService.criar(movimentacaoDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar movimentação sem usuário autenticado")
    void testCriarSemUsuarioAutenticado() {
        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> movimentacaoService.criar(movimentacaoDTO));
    }

    @Test
    @DisplayName("Deve deletar entrada e restaurar estoque")
    void testDeletarEntrada() {
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidadeMovimentacao(50);
        movimentacao.getObservacao();

        produto.setQuantidade(150);

        when(movimentacaoRepository.findById(1)).thenReturn(Optional.of(movimentacao));
        when(produtoRepository.save(any())).thenReturn(produto);

        movimentacaoService.deletar(1);

        assertEquals(100, produto.getQuantidade());
        verify(movimentacaoRepository, times(1)).delete(movimentacao);
    }

    @Test
    @DisplayName("Deve deletar saída e restaurar estoque")
    void testDeletarSaida() {
        movimentacao.setTipoMovimentacao(TipoMovimentacao.SAIDA);
        movimentacao.setQuantidadeMovimentacao(50);
        produto.setQuantidade(50);

        when(movimentacaoRepository.findById(1)).thenReturn(Optional.of(movimentacao));
        when(produtoRepository.save(any())).thenReturn(produto);

        movimentacaoService.deletar(1);

        assertEquals(100, produto.getQuantidade());
        verify(movimentacaoRepository, times(1)).delete(movimentacao);
    }

    @Test
    @DisplayName("Deve registrar movimentação com fornecedor")
    void testCriarComFornecedor() {
        movimentacaoDTO.setIdFornecedor(1);

        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(movimentacaoRepository.save(any())).thenReturn(movimentacao);
        when(produtoRepository.save(any())).thenReturn(produto);

        MovimentacaoResponseDTO resultado = movimentacaoService.criar(movimentacaoDTO);

        assertNotNull(resultado);
        verify(fornecedorRepository, times(1)).findById(1);
    }
}

