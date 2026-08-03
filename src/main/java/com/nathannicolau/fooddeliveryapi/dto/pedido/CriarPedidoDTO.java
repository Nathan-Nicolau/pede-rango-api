package com.nathannicolau.fooddeliveryapi.dto.pedido;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CriarPedidoDTO(
    Long clienteId,
    String nomeCliente,
    String enderecoEntrega,

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    List<CriarItemPedidoDTO> itens
) {}
