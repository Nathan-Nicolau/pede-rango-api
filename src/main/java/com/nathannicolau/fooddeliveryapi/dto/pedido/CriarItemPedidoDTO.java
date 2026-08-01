package com.nathannicolau.fooddeliveryapi.dto.pedido;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarItemPedidoDTO(
    @NotBlank(message = "Nome do item é obrigatório")
    String nome,

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade mínima é 1")
    Integer quantidade,

    @NotNull(message = "Preço unitário é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço unitário deve ser maior que zero")
    BigDecimal precoUnitario
) {}
