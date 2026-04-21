package com.gestorestoques.api.service;

import com.gestorestoques.api.model.PerfilUsuario;
import com.gestorestoques.api.model.Usuario;
import com.gestorestoques.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.default-admin.enabled:true}")
    private boolean defaultAdminEnabled;

    public DefaultAdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!defaultAdminEnabled) {
            return;
        }

        Usuario admin = usuarioRepository.findByNomeUsuario("adm").orElseGet(Usuario::new);
        admin.setNomeUsuario("adm");
        admin.setNomeCompleto("Administrador");
        admin.setPerfilUsuario(PerfilUsuario.ADMINISTRADOR);

        // Garante credencial padrao consistente em ambiente de desenvolvimento.
        String senhaAtual = admin.getSenhaUsuario();
        boolean senhaValida = senhaAtual != null
                && isBcryptHash(senhaAtual)
                && passwordEncoder.matches("1234", senhaAtual);
        if (!senhaValida) {
            admin.setSenhaUsuario(passwordEncoder.encode("1234"));
        }

        usuarioRepository.save(admin);
    }

    private boolean isBcryptHash(String senha) {
        return senha.startsWith("$2a$") || senha.startsWith("$2b$") || senha.startsWith("$2y$");
    }
}


