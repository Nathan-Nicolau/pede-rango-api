package com.nathannicolau.pederangoapi.dto.avaliacao;

import com.nathannicolau.pederangoapi.domain.avaliacao.AvaliacaoPedido;

import java.time.LocalDateTime;

public record RespostaAvaliacaoDTO(
    Long id,
    Long pedidoId,
    Long clienteId,
    Integer nota,
    String comentario,
    LocalDateTime criadoEm
) {
    public static RespostaAvaliacaoDTO daEntidade(AvaliacaoPedido avaliacao) {
        if (avaliacao == null) return null;
        return new RespostaAvaliacaoDTO(
            avaliacao.getId(),
            avaliacao.getPedido() != null ? avaliacao.getPedido().getId() : null,
            avaliacao.getCliente() != null ? avaliacao.getCliente().getId() : null,
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getCriadoEm()
        );
    }
}
