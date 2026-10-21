package com.nathannicolau.pederangoapi.dto.pedido;

import com.nathannicolau.pederangoapi.domain.pedido.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record CancelarPedidoDTO(
    @NotNull(message = "Motivo de cancelamento é obrigatório")
    MotivoCancelamento motivo,

    String observacoes
) {}
