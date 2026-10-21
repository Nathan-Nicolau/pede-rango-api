package com.nathannicolau.pederangoapi.dto.produto;

import com.nathannicolau.pederangoapi.domain.produto.Produto;
import java.math.BigDecimal;

public record RespostaProdutoDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    String categoria,
    Boolean ativo
) {
    public static RespostaProdutoDTO daEntidade(Produto produto) {
        return new RespostaProdutoDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            produto.getCategoria(),
            produto.getAtivo()
        );
    }
}
