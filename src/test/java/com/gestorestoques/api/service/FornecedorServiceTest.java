package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.FornecedorDTO;
import com.gestorestoques.api.dto.FornecedorResponseDTO;
import com.gestorestoques.api.model.Fornecedor;
import com.gestorestoques.api.repository.FornecedorRepository;
import com.gestorestoques.api.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do FornecedorService")
class FornecedorServiceTest {

    @Mock
    private FornecedorRepository fornecedorRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private FornecedorService fornecedorService;

    private FornecedorDTO fornecedorDTO;
    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        fornecedor = new Fornecedor();
        fornecedor.setIdFornecedor(1);
        fornecedor.setNomeFornecedor("Fornecedor A");
        fornecedor.setCnpj("12.345.678/0001-90");
        fornecedor.setContato("(11) 1234-5678");
        fornecedor.setEmail("contato@fornecedor.com");
        fornecedor.setDataCadastro(LocalDateTime.now());

        fornecedorDTO = new FornecedorDTO();
        fornecedorDTO.setNome("Fornecedor A");
        fornecedorDTO.setCnpj("12.345.678/0001-90");
        fornecedorDTO.setContato("(11) 1234-5678");
        fornecedorDTO.setEmail("contato@fornecedor.com");
    }

    @Test
    @DisplayName("Deve listar todos os fornecedores")
    void testListarTodos() {
        when(fornecedorRepository.findAll()).thenReturn(List.of(fornecedor));

        List<FornecedorResponseDTO> resultado = fornecedorService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Fornecedor A", resultado.get(0).getNome());
        verify(fornecedorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar fornecedor por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));

        FornecedorResponseDTO resultado = fornecedorService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Fornecedor A", resultado.getNome());
        verify(fornecedorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exception ao buscar fornecedor inexistente")
    void testBuscarPorIdNotFound() {
        when(fornecedorRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fornecedorService.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve criar fornecedor com sucesso")
    void testCriarSucesso() {
        when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);
        when(fornecedorRepository.save(any())).thenReturn(fornecedor);

        FornecedorResponseDTO resultado = fornecedorService.criar(fornecedorDTO);

        assertNotNull(resultado);
        assertEquals("Fornecedor A", resultado.getNome());
        verify(fornecedorRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exception ao criar fornecedor com nome vazio")
    void testCriarComNomeVazio() {
        fornecedorDTO.setNome("");

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar fornecedor com CNPJ inválido")
    void testCriarComCnpjInvalido() {
        fornecedorDTO.setCnpj("12345678");

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar fornecedor com CNPJ duplicado")
    void testCriarComCnpjDuplicado() {
        when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar fornecedor com email inválido")
    void testCriarComEmailInvalido() {
        fornecedorDTO.setEmail("email_invalido");

        when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve criar fornecedor sem email")
    void testCriarSemEmail() {
        fornecedorDTO.setEmail(null);

        when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);
        when(fornecedorRepository.save(any())).thenReturn(fornecedor);

        FornecedorResponseDTO resultado = fornecedorService.criar(fornecedorDTO);

        assertNotNull(resultado);
        verify(fornecedorRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve atualizar fornecedor com sucesso")
    void testAtualizarSucesso() {
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(fornecedorRepository.existsByCnpjAndIdFornecedorNot("12.345.678/0001-90", 1)).thenReturn(false);
        when(fornecedorRepository.save(any())).thenReturn(fornecedor);

        FornecedorResponseDTO resultado = fornecedorService.atualizar(1, fornecedorDTO);

        assertNotNull(resultado);
        verify(fornecedorRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve deletar fornecedor com sucesso")
    void testDeletarSucesso() {
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.existsByFornecedorIdFornecedor(1)).thenReturn(false);

        fornecedorService.deletar(1);

        verify(fornecedorRepository, times(1)).delete(fornecedor);
    }

    @Test
    @DisplayName("Deve lançar exception ao deletar fornecedor com produtos vinculados")
    void testDeletarComProdutos() {
        when(fornecedorRepository.findById(1)).thenReturn(Optional.of(fornecedor));
        when(produtoRepository.existsByFornecedorIdFornecedor(1)).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> fornecedorService.deletar(1));
    }

    @Test
    @DisplayName("Deve validar nome com menos de 2 caracteres")
    void testValidarNomeInsuficiente() {
        fornecedorDTO.setNome("A");

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve validar nome com mais de 120 caracteres")
    void testValidarNomeExcessivo() {
        fornecedorDTO.setNome("A".repeat(121));

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }

    @Test
    @DisplayName("Deve validar contato com mais de 120 caracteres")
    void testValidarContatoExcessivo() {
        fornecedorDTO.setContato("A".repeat(121));

        when(fornecedorRepository.existsByCnpj("12.345.678/0001-90")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> fornecedorService.criar(fornecedorDTO));
    }
}

