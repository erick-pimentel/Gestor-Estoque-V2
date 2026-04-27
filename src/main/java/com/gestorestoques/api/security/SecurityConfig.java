package com.gestorestoques.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/pages/**", "/HTML/**", "/JS/**", "/Style.css").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/produtos/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/produtos/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers(HttpMethod.PUT, "/api/produtos/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers(HttpMethod.DELETE, "/api/produtos/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers(HttpMethod.GET, "/api/fornecedores/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE", "GESTOR")
                        .requestMatchers(HttpMethod.POST, "/api/fornecedores/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers(HttpMethod.PUT, "/api/fornecedores/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers(HttpMethod.DELETE, "/api/fornecedores/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE")
                        .requestMatchers("/api/movimentacoes/**").hasAnyRole("ADMINISTRADOR", "OPERADOR_DE_ESTOQUE", "GESTOR")
                        .requestMatchers("/api/relatorios/**").hasAnyRole("ADMINISTRADOR", "GESTOR")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

