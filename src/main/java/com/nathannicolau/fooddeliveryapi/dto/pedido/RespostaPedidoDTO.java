package com.nathannicolau.fooddeliveryapi.dto.pedido;

import com.nathannicolau.fooddeliveryapi.domain.pedido.Pedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RespostaPedidoDTO(
    Long id,
    String codigo,
    Long clienteId,
    String nomeCliente,
    String enderecoEntrega,
    StatusPedido status,
    Long entregadorId,
    String nomeEntregador,
    BigDecimal valorTotal,
    List<RespostaItemPedidoDTO> itens,
    RespostaCancelamentoDTO cancelamento,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public static RespostaPedidoDTO daEntidade(Pedido pedido) {
        List<RespostaItemPedidoDTO> itensDTO = pedido.getItens() != null
            ? pedido.getItens().stream().map(RespostaItemPedidoDTO::daEntidade).toList()
            : List.of();

        String nomeEntregador = (pedido.getEntregador() != null && pedido.getEntregador().getUsuario() != null)
            ? pedido.getEntregador().getUsuario().getNome()
            : null;

        Long entregadorId = pedido.getEntregador() != null
            ? pedido.getEntregador().getId()
            : null;

        Long clienteId = pedido.getCliente() != null
            ? pedido.getCliente().getId()
            : null;

        return new RespostaPedidoDTO(
            pedido.getId(),
            pedido.getCodigo(),
            clienteId,
            pedido.getNomeCliente(),
            pedido.getEnderecoEntrega(),
            pedido.getStatus(),
            entregadorId,
            nomeEntregador,
            pedido.getValorTotal(),
            itensDTO,
            RespostaCancelamentoDTO.daEntidade(pedido.getCancelamento()),
            pedido.getCriadoEm(),
            pedido.getAtualizadoEm()
        );
    }
}
