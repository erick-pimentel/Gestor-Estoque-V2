package com.gestorestoques.api.repository;

import com.gestorestoques.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    boolean existsByNomeUsuario(String nomeUsuario);

    boolean existsByNomeUsuarioAndIdUsuarioNot(String nomeUsuario, Integer idUsuario);
}

