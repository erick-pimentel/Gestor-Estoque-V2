package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.UsuarioDTO;
import com.gestorestoques.api.dto.UsuarioResponseDTO;
import com.gestorestoques.api.model.PerfilUsuario;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioService")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNomeUsuario("admin");
        usuario.setNomeCompleto("Administrador");
        usuario.setSenhaUsuario("$2a$10$encoded_password");
        usuario.setPerfilUsuario(PerfilUsuario.ADMINISTRADOR);
        usuario.setDataCadastro(LocalDateTime.now());

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNomeUsuario("admin");
        usuarioDTO.setNomeCompleto("Administrador");
        usuarioDTO.setSenhaUsuario("senha123");
        usuarioDTO.setPerfilUsuario(PerfilUsuario.ADMINISTRADOR);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void testListarTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("admin", resultado.get(0).getNomeUsuario());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("admin", resultado.getNomeUsuario());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Deve lançar exception ao buscar usuário inexistente")
    void testBuscarPorIdNotFound() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> usuarioService.buscarPorId(999));
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarSucesso() {
        when(usuarioRepository.existsByNomeUsuario("admin")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$encoded_password");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.criar(usuarioDTO);

        assertNotNull(resultado);
        assertEquals("admin", resultado.getNomeUsuario());
        verify(usuarioRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode("senha123");
    }

    @Test
    @DisplayName("Deve lançar exception ao criar usuário com nome vazio")
    void testCriarComNomeVazio() {
        usuarioDTO.setNomeUsuario("");

        assertThrows(ResponseStatusException.class, () -> usuarioService.criar(usuarioDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar usuário sem senha")
    void testCriarSemSenha() {
        usuarioDTO.setSenhaUsuario("");

        assertThrows(ResponseStatusException.class, () -> usuarioService.criar(usuarioDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar usuário com nome duplicado")
    void testCriarComNomeDuplicado() {
        when(usuarioRepository.existsByNomeUsuario("admin")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> usuarioService.criar(usuarioDTO));
    }

    @Test
    @DisplayName("Deve lançar exception ao criar usuário sem perfil")
    void testCriarSemPerfil() {
        usuarioDTO.setPerfilUsuario(null);

        assertThrows(ResponseStatusException.class, () -> usuarioService.criar(usuarioDTO));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarSucesso() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByNomeUsuarioAndIdUsuarioNot("admin", 1)).thenReturn(false);
        when(passwordEncoder.encode("nova_senha")).thenReturn("$2a$10$new_encoded");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        usuarioDTO.setSenhaUsuario("nova_senha");
        UsuarioResponseDTO resultado = usuarioService.atualizar(1, usuarioDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode("nova_senha");
    }

    @Test
    @DisplayName("Deve atualizar usuário sem mudar senha")
    void testAtualizarSemMudarSenha() {
        usuarioDTO.setSenhaUsuario("");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByNomeUsuarioAndIdUsuarioNot("admin", 1)).thenReturn(false);
        when(usuarioRepository.save(any())).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.atualizar(1, usuarioDTO);

        assertNotNull(resultado);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void testDeletarSucesso() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        usuarioService.deletar(1);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    @DisplayName("Deve lançar exception ao deletar usuário inexistente")
    void testDeletarInexistente() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> usuarioService.deletar(999));
    }

    @Test
    @DisplayName("Deve validar nome de usuário vazio")
    void testValidarNomeUsuarioVazio() {
        usuarioDTO.setNomeUsuario(null);

        assertThrows(ResponseStatusException.class, () -> usuarioService.criar(usuarioDTO));
    }

    @Test
    @DisplayName("Deve criar usuário com diferentes perfis")
    void testCriarComDiferentesPerfis() {
        when(usuarioRepository.existsByNomeUsuario(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usuarioRepository.save(any())).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setIdUsuario(1);
            return u;
        });

        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            usuarioDTO.setPerfilUsuario(perfil);
            UsuarioResponseDTO resultado = usuarioService.criar(usuarioDTO);
            assertNotNull(resultado);
            assertEquals(perfil, resultado.getPerfilUsuario());
        }
    }
}

