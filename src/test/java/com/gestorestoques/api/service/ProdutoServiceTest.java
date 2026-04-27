package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.ProdutoDTO;
import com.gestorestoques.api.dto.ProdutoResponseDTO;
import com.gestorestoques.api.model.Fornecedor;
import com.gestorestoques.api.model.Produto;
import com.gestorestoques.api.model.Usuario;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProdutoService")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @InjectMocks
    private ProdutoService produtoService;

    private ProdutoDTO produtoDTO;
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
        produto.setFornecedor(fornecedor);
        produto.setUsuario(usuario);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Produto Test");
        produtoDTO.setCodigo("PROD001");
        produtoDTO.setQuantidade(100);
        produtoDTO.setPrecoCusto(BigDecimal.valueOf(10.00));
        produtoDTO.setPrecoVenda(BigDecimal.valueOf(15.00));
        produtoDTO.setIdFornecedor(1);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void testListarTodos() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<ProdutoResponseDTO> resultado = produtoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Produto Test", resultado.get(0).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));

        ProdutoResponseDTO resultado = produtoService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Produto Test", resultado.getNome());
        verify(produtoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exception ao buscar produto inexistente")
    void testBuscarPorIdNotFound() {
        when(produtoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> produtoService.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void testCriarSucesso() {
        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(fornecedorRepository.existsById(1)).thenReturn(true);
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.existsByCodigoProduto("PROD001")).thenReturn(false);
        when(produtoRepository.save(any())).thenReturn(produto);

        ProdutoResponseDTO resultado = produtoService.criar(produtoDTO);

        assertNotNull(resultado);
        assertEquals("Produto Test", resultado.getNome());
        verify(produtoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exception ao criar produto com nome vazio")
    void testCriarComNomeVazio() {
        produtoDTO.setNome("");

        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar produto com código duplicado")
    void testCriarComCodigoDuplicado() {
        when(produtoRepository.existsByCodigoProduto("PROD001")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar produto com preço de venda menor que custo")
    void testCriarComPrecoVendaMenorQueCusto() {
        produtoDTO.setPrecoVenda(BigDecimal.valueOf(5.00));
        produtoDTO.setPrecoCusto(BigDecimal.valueOf(10.00));


        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void testAtualizarSucesso() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(usuarioAutenticadoService.getUsuarioLogado()).thenReturn(usuario);
        when(fornecedorRepository.existsById(1)).thenReturn(true);
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.existsByCodigoProdutoAndIdProdutoNot("PROD001", 1)).thenReturn(false);
        when(produtoRepository.save(any())).thenReturn(produto);

        ProdutoResponseDTO resultado = produtoService.atualizar(1, produtoDTO);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void testDeletarSucesso() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.existsByProdutoIdProduto(1)).thenReturn(false);

        produtoService.deletar(1);

        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    @DisplayName("Deve lançar exception ao deletar produto com movimentações vinculadas")
    void testDeletarComMovimentacoes() {
        when(produtoRepository.findById(1)).thenReturn(Optional.of(produto));
        when(movimentacaoRepository.existsByProdutoIdProduto(1)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> produtoService.deletar(1));
    }

    @Test
    @DisplayName("Deve validar quantidade negativa")
    void testValidarQuantidadeNegativa() {
        produtoDTO.setQuantidade(-10);

        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }

    @Test
    @DisplayName("Deve validar preço de custo zero")
    void testValidarPrecoCustoZero() {
        produtoDTO.setPrecoCusto(BigDecimal.ZERO);

        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }

    @Test
    @DisplayName("Deve validar fornecedor inexistente")
    void testValidarFornecedorInexistente() {
        when(fornecedorRepository.existsById(1)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> produtoService.criar(produtoDTO));
    }
}

