package com.nathannicolau.pederangoapi.config;

import com.nathannicolau.pederangoapi.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Componente de inicialização que garante que todas as senhas no banco
 * estejam criptografadas com BCrypt. Converte automaticamente senhas
 * em texto limpo (ex: vindas do data.sql seed) para hash BCrypt.
 */
@Component
public class InicializadorSenhas implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorSenhas.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public InicializadorSenhas(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        usuarioRepository.findAll().forEach(usuario -> {
            String senha = usuario.getSenha();
            // Senhas BCrypt sempre começam com "$2a$", "$2b$" ou "$2y$"
            if (senha != null && !senha.startsWith("$2a$") && !senha.startsWith("$2b$") && !senha.startsWith("$2y$")) {
                String senhaHash = passwordEncoder.encode(senha);
                usuario.setSenha(senhaHash);
                usuarioRepository.save(usuario);
                log.info("Senha do usuário '{}' convertida para BCrypt.", usuario.getEmail());
            }
        });
    }
}
