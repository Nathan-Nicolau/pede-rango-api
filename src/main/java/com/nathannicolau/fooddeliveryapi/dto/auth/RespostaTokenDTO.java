package com.nathannicolau.fooddeliveryapi.dto.auth;

import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;

public record RespostaTokenDTO(
    String token,
    String tipoToken,
    String email,
    PerfilUsuario perfil
) {
    public RespostaTokenDTO(String token, String email, PerfilUsuario perfil) {
        this(token, "Bearer", email, perfil);
    }
}
