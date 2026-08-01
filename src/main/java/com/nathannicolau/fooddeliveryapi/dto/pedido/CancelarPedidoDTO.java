package com.nathannicolau.fooddeliveryapi.dto.pedido;

import com.nathannicolau.fooddeliveryapi.domain.pedido.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record CancelarPedidoDTO(
    @NotNull(message = "Motivo de cancelamento é obrigatório")
    MotivoCancelamento motivo,

    String observacoes
) {}
