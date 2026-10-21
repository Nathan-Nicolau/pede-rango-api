package com.nathannicolau.pederangoapi.dto.usuario;

import com.nathannicolau.pederangoapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.pederangoapi.domain.usuario.Usuario;

import java.time.LocalDateTime;

public record RespostaUsuarioDTO(
    Long id,
    String nome,
    String email,
    PerfilUsuario perfil,
    Boolean ativo,
    LocalDateTime criadoEm
) {
    public static RespostaUsuarioDTO daEntidade(Usuario usuario) {
        return new RespostaUsuarioDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getPerfil(),
            usuario.getAtivo(),
            usuario.getCriadoEm()
        );
    }
}
