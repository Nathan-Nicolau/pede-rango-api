package com.nathannicolau.pederangoapi.dto.pedido;

import com.nathannicolau.pederangoapi.domain.pedido.ItemPedido;

import java.math.BigDecimal;

public record RespostaItemPedidoDTO(
    Long id,
    Long produtoId,
    String nome,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal,
    String observacao
) {
    public static RespostaItemPedidoDTO daEntidade(ItemPedido item) {
        BigDecimal subtotal = item.getPrecoUnitario() != null && item.getQuantidade() != null
            ? item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
            : BigDecimal.ZERO;

        Long produtoId = item.getProduto() != null ? item.getProduto().getId() : null;

        return new RespostaItemPedidoDTO(
            item.getId(),
            produtoId,
            item.getNome(),
            item.getQuantidade(),
            item.getPrecoUnitario(),
            subtotal,
            item.getObservacao()
        );
    }
}
