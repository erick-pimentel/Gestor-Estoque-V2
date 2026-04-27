package com.gestorestoques.api.service;

import com.gestorestoques.api.dto.UsuarioDTO;
import com.gestorestoques.api.dto.UsuarioResponseDTO;
import com.gestorestoques.api.model.PerfilUsuario;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioDTO dto) {
        validar(dto, null, true);
        Usuario usuario = new Usuario();
        preencherUsuario(usuario, dto, true);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Integer id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        validar(dto, id, false);
        preencherUsuario(usuario, dto, false);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null
                && authentication.getName().equals(usuario.getNomeUsuario())
                && usuario.getPerfilUsuario() == PerfilUsuario.ADMINISTRADOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrador nao pode excluir a propria conta");
        }

        usuarioRepository.delete(usuario);
    }

    private void validar(UsuarioDTO dto, Integer idAtualizacao, boolean criando) {
        if (dto == null || dto.getNomeUsuario() == null || dto.getNomeUsuario().isBlank() || dto.getPerfilUsuario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nomeUsuario e perfilUsuario sao obrigatorios");
        }

        if (criando && (dto.getSenhaUsuario() == null || dto.getSenhaUsuario().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "senhaUsuario e obrigatoria");
        }

        boolean duplicado = idAtualizacao == null
                ? usuarioRepository.existsByNomeUsuario(dto.getNomeUsuario())
                : usuarioRepository.existsByNomeUsuarioAndIdUsuarioNot(dto.getNomeUsuario(), idAtualizacao);
        if (duplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de usuario ja cadastrado");
        }
    }

    private void preencherUsuario(Usuario usuario, UsuarioDTO dto, boolean criando) {
        usuario.setNomeUsuario(dto.getNomeUsuario().trim());
        usuario.setPerfilUsuario(dto.getPerfilUsuario());
        usuario.setNomeCompleto(dto.getNomeCompleto());

        if (criando) {
            usuario.setSenhaUsuario(passwordEncoder.encode(dto.getSenhaUsuario()));
        } else if (dto.getSenhaUsuario() != null && !dto.getSenhaUsuario().isBlank()) {
            usuario.setSenhaUsuario(passwordEncoder.encode(dto.getSenhaUsuario()));
        }
    }

    private UsuarioResponseDTO toResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getIdUsuario());
        dto.setNomeUsuario(usuario.getNomeUsuario());
        dto.setPerfilUsuario(usuario.getPerfilUsuario());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setDataCadastro(usuario.getDataCadastro());
        return dto;
    }
}

