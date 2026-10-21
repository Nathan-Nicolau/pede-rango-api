package com.nathannicolau.pederangoapi.dto.cliente;

import com.nathannicolau.pederangoapi.domain.cliente.Cliente;

public record RespostaClienteDTO(
    Long id,
    Long usuarioId,
    String nome,
    String email,
    String cpf,
    String telefone,
    String enderecoPadrao,
    Boolean ativo
) {
    public static RespostaClienteDTO daEntidade(Cliente cliente) {
        return new RespostaClienteDTO(
            cliente.getId(),
            cliente.getUsuario() != null ? cliente.getUsuario().getId() : null,
            cliente.getUsuario() != null ? cliente.getUsuario().getNome() : null,
            cliente.getUsuario() != null ? cliente.getUsuario().getEmail() : null,
            cliente.getCpf(),
            cliente.getTelefone(),
            cliente.getEnderecoPadrao(),
            cliente.getAtivo()
        );
    }
}
