package com.nathannicolau.fooddeliveryapi.dto.relatorio;

public record DistribuicaoStatusDTO(
    String status,
    String label,
    long quantidade
) {}
