package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.auth.RegistroDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(RegistroDTO dto) {
        // 1. Validar se o e-mail informado já está cadastrado no sistema
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("O e-mail '" + dto.email() + "' já está cadastrado no sistema.");
        }

        // 2. Instanciar a nova entidade de Usuario
        Usuario usuario = new Usuario(
            dto.nome(),
            dto.email(),
            dto.senha(),
            dto.perfil()
        );

        // 3. Persistir no banco de dados
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado com o e-mail informado: " + email));
    }
}
