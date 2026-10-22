package com.nathannicolau.pederangoapi.dto.pedido;

import com.nathannicolau.pederangoapi.domain.pedido.CancelamentoPedido;
import com.nathannicolau.pederangoapi.domain.pedido.MotivoCancelamento;

import java.time.LocalDateTime;

public record RespostaCancelamentoDTO(
    Long id,
    MotivoCancelamento motivo,
    String observacoes,
    LocalDateTime canceladoEm
) {
    public static RespostaCancelamentoDTO daEntidade(CancelamentoPedido cancelamento) {
        if (cancelamento == null) return null;
        return new RespostaCancelamentoDTO(
            cancelamento.getId(),
            cancelamento.getMotivo(),
            cancelamento.getObservacoes(),
            cancelamento.getCanceladoEm()
        );
    }
}
