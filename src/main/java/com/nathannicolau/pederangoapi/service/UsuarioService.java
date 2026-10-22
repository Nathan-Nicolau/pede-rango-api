package com.nathannicolau.pederangoapi.service;

import com.nathannicolau.pederangoapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.pederangoapi.domain.usuario.Usuario;
import com.nathannicolau.pederangoapi.dto.auth.RegistroDTO;
import com.nathannicolau.pederangoapi.dto.usuario.RespostaUsuarioDTO;
import com.nathannicolau.pederangoapi.exception.RegraDeNegocioException;
import com.nathannicolau.pederangoapi.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrarUsuario(RegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("O e-mail '" + dto.email() + "' já está cadastrado no sistema.");
        }

        String senhaFinal = dto.senha();
        // Criptografa caso a senha ainda esteja em texto puro (não iniciada com hash BCrypt $2a$/$2b$/$2y$)
        if (senhaFinal != null && !senhaFinal.startsWith("$2a$") && !senhaFinal.startsWith("$2b$") && !senhaFinal.startsWith("$2y$")) {
            senhaFinal = passwordEncoder.encode(senhaFinal);
        }

        Usuario usuario = new Usuario(
            dto.nome(),
            dto.email(),
            senhaFinal,
            dto.perfil()
        );

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado com o e-mail informado: " + email));
    }

    @Transactional(readOnly = true)
    public RespostaUsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado com ID: " + id));
        return RespostaUsuarioDTO.daEntidade(usuario);
    }

    @Transactional(readOnly = true)
    public Page<RespostaUsuarioDTO> listarTodosPaginado(Pageable pageable) {
        return usuarioRepository.findByAtivoTrue(pageable)
            .map(RespostaUsuarioDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public Page<RespostaUsuarioDTO> listarPorPerfilPaginado(PerfilUsuario perfil, Pageable pageable) {
        return usuarioRepository.findByPerfilAndAtivoTrue(perfil, pageable)
            .map(RespostaUsuarioDTO::daEntidade);
    }
}
