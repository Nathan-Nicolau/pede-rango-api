package com.nathannicolau.fooddeliveryapi.dto.pedido;

import com.nathannicolau.fooddeliveryapi.domain.pedido.FormaPagamento;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

public record CriarPedidoDTO(
    Long clienteId,
    String nomeCliente,
    String enderecoEntrega,
    FormaPagamento formaPagamento,
    BigDecimal trocoPara,

    @NotEmpty(message = "O pedido deve conter pelo menos um item")
    List<CriarItemPedidoDTO> itens
) {}
