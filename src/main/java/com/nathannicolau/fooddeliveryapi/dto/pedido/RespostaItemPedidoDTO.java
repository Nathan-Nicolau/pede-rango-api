package com.nathannicolau.fooddeliveryapi.dto.pedido;

import com.nathannicolau.fooddeliveryapi.domain.pedido.ItemPedido;

import java.math.BigDecimal;

public record RespostaItemPedidoDTO(
    Long id,
    String nome,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal
) {
    public static RespostaItemPedidoDTO daEntidade(ItemPedido item) {
        BigDecimal subtotal = item.getPrecoUnitario() != null && item.getQuantidade() != null
            ? item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
            : BigDecimal.ZERO;

        return new RespostaItemPedidoDTO(
            item.getId(),
            item.getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(),
            subtotal
        );
    }
}
