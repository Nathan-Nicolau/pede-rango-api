package com.nathannicolau.fooddeliveryapi.dto.entregador;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarEntregadorDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "O e-mail informado não possui um formato válido")
    String email,

    @NotBlank(message = "A senha inicial é obrigatória")
    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres")
    String senha,

    @NotBlank(message = "O telefone é obrigatório")
    String telefone,

    @NotBlank(message = "O veículo é obrigatório")
    String veiculo,

    String placa
) {}
