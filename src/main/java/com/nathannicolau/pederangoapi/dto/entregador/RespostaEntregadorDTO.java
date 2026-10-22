package com.nathannicolau.pederangoapi.dto.entregador;

import com.nathannicolau.pederangoapi.domain.entregador.Entregador;

public record RespostaEntregadorDTO(
    Long id,
    Long usuarioId,
    String nome,
    String email,
    String telefone,
    String veiculo,
    String placa,
    Boolean disponivel,
    Boolean ativo
) {
    public static RespostaEntregadorDTO daEntidade(Entregador entregador) {
        return new RespostaEntregadorDTO(
            entregador.getId(),
            entregador.getUsuario() != null ? entregador.getUsuario().getId() : null,
            entregador.getUsuario() != null ? entregador.getUsuario().getNome() : null,
            entregador.getUsuario() != null ? entregador.getUsuario().getEmail() : null,
            entregador.getTelefone(),
            entregador.getVeiculo(),
            entregador.getPlaca(),
            entregador.getDisponivel(),
            entregador.getAtivo()
        );
    }
}
