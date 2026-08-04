package com.nathannicolau.fooddeliveryapi.dto.auth;

import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "O e-mail informado não possui um formato válido (exemplo: usuario@dominio.com)")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
    String senha,

    PerfilUsuario perfil,

    String cpf,

    String telefone,

    String enderecoPadrao
) {
    public RegistroDTO(String nome, String email, String senha, PerfilUsuario perfil) {
        this(nome, email, senha, perfil, null, null, null);
    }
}
