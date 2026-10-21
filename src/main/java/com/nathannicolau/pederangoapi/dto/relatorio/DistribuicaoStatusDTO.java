package com.nathannicolau.pederangoapi.dto.relatorio;

public record DistribuicaoStatusDTO(
    String status,
    String label,
    long quantidade
) {}
