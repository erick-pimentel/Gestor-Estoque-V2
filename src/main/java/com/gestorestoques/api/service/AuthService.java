package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.LoginRequestDTO;
import com.gestorestoques.api.dto.LoginResponseDTO;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.UsuarioRepository;
import com.gestorestoques.api.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        if (request.getNomeUsuario() == null || request.getNomeUsuario().isBlank() ||
                request.getSenhaUsuario() == null || request.getSenhaUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome de usuario e senha sao obrigatorios");
        }

        String nomeUsuario = request.getNomeUsuario().trim();
        String senhaInformada = request.getSenhaUsuario();

        Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas"));

        if (!senhaConfere(usuario, senhaInformada)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas");
        }

        String token = jwtUtil.generateToken(usuario.getNomeUsuario(), usuario.getPerfilUsuario().getDescricao());
        return new LoginResponseDTO(token, usuario.getPerfilUsuario().getDescricao(), usuario.getNomeUsuario());
    }

    private boolean senhaConfere(Usuario usuario, String senhaInformada) {
        String senhaPersistida = usuario.getSenhaUsuario();
        if (senhaPersistida == null || senhaPersistida.isBlank()) {
            return false;
        }

        if (senhaPersistida.startsWith("$2a$") || senhaPersistida.startsWith("$2b$") || senhaPersistida.startsWith("$2y$")) {
            return passwordEncoder.matches(senhaInformada, senhaPersistida);
        }

        // Compatibilidade com registros legados em texto puro: valida e atualiza para BCrypt.
        if (senhaInformada.equals(senhaPersistida)) {
            usuario.setSenhaUsuario(passwordEncoder.encode(senhaInformada));
            usuarioRepository.save(usuario);
            return true;
        }

        return false;
    }
}

