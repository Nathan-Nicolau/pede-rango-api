package com.nathannicolau.pederangoapi.dto.relatorio;

import java.math.BigDecimal;

public record DistribuicaoPagamentoDTO(
    String formaPagamento,
    String label,
    long quantidade,
    BigDecimal total
) {}
