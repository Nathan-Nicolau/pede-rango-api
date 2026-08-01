package com.nathannicolau.fooddeliveryapi.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CriarPedidoDTO(
    @NotBlank(message = "Nome do cliente é obrigatório")
    String nomeCliente,

    @NotBlank(message = "Endereço de entrega é obrigatório")
    String enderecoEntrega,

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    @Valid
    List<CriarItemPedidoDTO> itens
) {}
