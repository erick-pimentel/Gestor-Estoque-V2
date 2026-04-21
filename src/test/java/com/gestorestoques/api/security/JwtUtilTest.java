package com.gestorestoques.api.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void deveGerarEValidarToken() {
        JwtUtil jwtUtil = new JwtUtil("01234567890123456789012345678901", 3_600_000);

        String token = jwtUtil.generateToken("admin", "Administrador");

        Assertions.assertNotNull(token);
        Assertions.assertEquals("admin", jwtUtil.extractNomeUsuario(token));
        Assertions.assertEquals("Administrador", jwtUtil.extractPerfil(token));
        Assertions.assertTrue(jwtUtil.isTokenValid(token, "admin"));
    }
}

