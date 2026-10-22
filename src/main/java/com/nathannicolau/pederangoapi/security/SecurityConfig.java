package com.nathannicolau.pederangoapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permite sem autenticação os endpoints de login, cadastro de usuário, checagem de e-mail, documentação Swagger e Actuator
                .requestMatchers(
                    "/auth/login",
                    "/auth/registrar",
                    "/auth/register",
                    "/auth/checar-email",
                    "/auth/checar-cpf",
                    "/auth/checar-telefone",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/configuracoes").permitAll()
                // Permite requisições pre-flight de CORS do navegador
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // TODOS os demais endpoints (pedidos, entregadores, etc.) exigem autenticação via Token JWT
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Ingress configurado com a aplicação em K8s e o localhost para desenvolvimento
        configuration.setAllowedOrigins(List.of(
                "http://app.pede-rango.local",
                "http://api.pede-rango.local",
                "http://app.food-delivery.local",
                "http://api.food-delivery.local",
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost",
                "http://127.0.0.1"
        ));
        configuration.setAllowedOriginPatterns(List.of(
                "http://*.pede-rango.local",
                "http://*.food-delivery.local",
                "http://*.nip.io",
                "http://*.nip.io:*",
                "http://192.168.*.*",
                "http://192.168.*.*:*",
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
