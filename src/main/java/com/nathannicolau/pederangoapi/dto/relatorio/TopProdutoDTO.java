package com.nathannicolau.pederangoapi.dto.relatorio;

import java.math.BigDecimal;

public record TopProdutoDTO(
    Long produtoId,
    String nome,
    String categoria,
    int quantidadeVendida,
    BigDecimal totalFaturado
) {}
