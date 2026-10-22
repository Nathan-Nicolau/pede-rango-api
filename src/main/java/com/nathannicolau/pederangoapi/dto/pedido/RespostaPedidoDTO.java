package com.nathannicolau.pederangoapi.dto.pedido;

import com.nathannicolau.pederangoapi.domain.pedido.FormaPagamento;
import com.nathannicolau.pederangoapi.domain.pedido.Pedido;
import com.nathannicolau.pederangoapi.domain.pedido.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RespostaPedidoDTO(
    Long id,
    String codigo,
    Long clienteId,
    String nomeCliente,
    String telefoneCliente,
    String enderecoEntrega,
    StatusPedido status,
    FormaPagamento formaPagamento,
    BigDecimal trocoPara,
    BigDecimal taxaEntrega,
    Long entregadorId,
    String nomeEntregador,
    String telefoneEntregador,
    BigDecimal valorTotal,
    List<RespostaItemPedidoDTO> itens,
    RespostaCancelamentoDTO cancelamento,
    com.nathannicolau.pederangoapi.dto.avaliacao.RespostaAvaliacaoDTO avaliacao,
    LocalDateTime criadoEm,
    LocalDateTime dataEntrega,
    LocalDateTime atualizadoEm
) {
    public static RespostaPedidoDTO daEntidade(Pedido pedido) {
        List<RespostaItemPedidoDTO> itensDTO = pedido.getItens() != null
            ? pedido.getItens().stream().map(RespostaItemPedidoDTO::daEntidade).toList()
            : List.of();

        String nomeEntregador = (pedido.getEntregador() != null && pedido.getEntregador().getUsuario() != null)
            ? pedido.getEntregador().getUsuario().getNome()
            : null;

        String telefoneEntregador = pedido.getEntregador() != null
            ? pedido.getEntregador().getTelefone()
            : null;

        Long entregadorId = pedido.getEntregador() != null
            ? pedido.getEntregador().getId()
            : null;

        Long clienteId = pedido.getCliente() != null
            ? pedido.getCliente().getId()
            : null;

        String telefoneCliente = pedido.getCliente() != null
            ? pedido.getCliente().getTelefone()
            : null;

        return new RespostaPedidoDTO(
            pedido.getId(),
            pedido.getCodigo(),
            clienteId,
            pedido.getNomeCliente(),
            telefoneCliente,
            pedido.getEnderecoEntrega(),
            pedido.getStatus(),
            pedido.getFormaPagamento(),
            pedido.getTrocoPara(),
            pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega() : BigDecimal.ZERO,
            entregadorId,
            nomeEntregador,
            telefoneEntregador,
            pedido.getValorTotal(),
            itensDTO,
            RespostaCancelamentoDTO.daEntidade(pedido.getCancelamento()),
            com.nathannicolau.pederangoapi.dto.avaliacao.RespostaAvaliacaoDTO.daEntidade(pedido.getAvaliacao()),
            pedido.getCriadoEm(),
            pedido.getDataEntrega(),
            pedido.getAtualizadoEm()
        );
    }
}
