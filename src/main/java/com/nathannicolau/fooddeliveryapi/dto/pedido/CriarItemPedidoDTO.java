package com.nathannicolau.fooddeliveryapi.dto.pedido;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record CriarItemPedidoDTO(
    Long produtoId,
    String nome,
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1")
    Integer quantidade,
    BigDecimal precoUnitario,
    String observacao
) {}
