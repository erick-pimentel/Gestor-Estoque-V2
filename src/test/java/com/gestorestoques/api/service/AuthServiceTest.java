package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.LoginRequestDTO;
import com.gestorestoques.api.dto.LoginResponseDTO;
import com.gestorestoques.api.model.PerfilUsuario;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.UsuarioRepository;
import com.gestorestoques.api.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthService")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private LoginRequestDTO loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNomeUsuario("admin");
        usuario.setSenhaUsuario("$2a$10$encoded_password");
        usuario.setPerfilUsuario(PerfilUsuario.ADMINISTRADOR);

        loginRequest = new LoginRequestDTO();
        loginRequest.setNomeUsuario("admin");
        loginRequest.setSenhaUsuario("senha123");
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void testLoginSucesso() {
        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "Administrador")).thenReturn("token_jwt_valido");

        LoginResponseDTO resultado = authService.login(loginRequest);

        assertNotNull(resultado);
        assertEquals("token_jwt_valido", resultado.getToken());
        assertEquals("admin", resultado.getNomeUsuario());
        verify(usuarioRepository, times(1)).findByNomeUsuario("admin");
        verify(passwordEncoder, times(1)).matches("senha123", "$2a$10$encoded_password");
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login com usuário inexistente")
    void testLoginUsuarioInexistente() {
        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login com senha incorreta")
    void testLoginSenhaIncorreta() {
        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$encoded_password")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login sem nome de usuário")
    void testLoginSemNomeUsuario() {
        loginRequest.setNomeUsuario("");

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login sem senha")
    void testLoginSemSenha() {
        loginRequest.setSenhaUsuario("");

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login com nome de usuário nulo")
    void testLoginComNomeNulo() {
        loginRequest.setNomeUsuario(null);

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve lançar exception ao fazer login com senha nula")
    void testLoginComSenhaNula() {
        loginRequest.setSenhaUsuario(null);

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("Deve atualizar senha legada de texto puro para BCrypt")
    void testLoginComSenhaLegadaTextoPlano() {
        // Simula senha armazenada em texto puro (compatibilidade com dados legados)
        usuario.setSenhaUsuario("senha123");

        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("senha123")).thenReturn("$2a$10$nova_senha_bcrypt");
        when(jwtUtil.generateToken("admin", "Administrador")).thenReturn("token_jwt");

        LoginResponseDTO resultado = authService.login(loginRequest);
        assertNotNull(resultado);
        assertEquals("token_jwt", resultado.getToken());
    }

    @Test
    @DisplayName("Deve fazer login com diferentes perfis")
    void testLoginComDiferentesPerfis() {
        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            usuario.setPerfilUsuario(perfil);

            when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));
            when(passwordEncoder.matches("senha123", "$2a$10$encoded_password")).thenReturn(true);
            when(jwtUtil.generateToken("admin", perfil.getDescricao())).thenReturn("token_" + perfil.name());

            LoginResponseDTO resultado = authService.login(loginRequest);

            assertNotNull(resultado);
            assertEquals(perfil.getDescricao(), resultado.getPerfil());
        }
    }

    @Test
    @DisplayName("Deve trimmar nome de usuário antes de buscar")
    void testLoginComEspacosNome() {
        loginRequest.setNomeUsuario("  admin  ");

        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "$2a$10$encoded_password")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "Administrador")).thenReturn("token");

        LoginResponseDTO resultado = authService.login(loginRequest);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findByNomeUsuario("admin");
    }

    @Test
    @DisplayName("Deve rejeitar login com senha nula no banco")
    void testLoginComSenhaNulaNoBanco() {
        usuario.setSenhaUsuario(null);

        when(usuarioRepository.findByNomeUsuario("admin")).thenReturn(Optional.of(usuario));

        assertThrows(ResponseStatusException.class, () -> authService.login(loginRequest));
    }
}

