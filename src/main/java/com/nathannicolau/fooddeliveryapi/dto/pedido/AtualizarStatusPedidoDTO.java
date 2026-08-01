package com.nathannicolau.fooddeliveryapi.dto.pedido;

import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoDTO(
    @NotNull(message = "Status do pedido é obrigatório")
    StatusPedido status
) {}
