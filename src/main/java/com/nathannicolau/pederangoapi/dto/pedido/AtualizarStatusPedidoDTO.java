package com.nathannicolau.pederangoapi.dto.pedido;

import com.nathannicolau.pederangoapi.domain.pedido.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoDTO(
    @NotNull(message = "Status do pedido é obrigatório")
    StatusPedido status
) {}
