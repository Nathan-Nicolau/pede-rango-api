package com.nathannicolau.fooddeliveryapi.dto.entregador;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarEntregadorDTO(
    @NotNull(message = "ID do usuário é obrigatório")
    Long usuarioId,

    @NotBlank(message = "Telefone é obrigatório")
    String telefone,

    @NotBlank(message = "Veículo é obrigatório")
    String veiculo,

    String placa
) {}
