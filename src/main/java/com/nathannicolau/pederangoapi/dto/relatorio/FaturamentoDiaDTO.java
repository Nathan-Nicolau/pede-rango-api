package com.nathannicolau.pederangoapi.dto.relatorio;

import java.math.BigDecimal;

public record FaturamentoDiaDTO(
    String data,
    String diaSemana,
    BigDecimal faturamento,
    long quantidadePedidos
) {}
