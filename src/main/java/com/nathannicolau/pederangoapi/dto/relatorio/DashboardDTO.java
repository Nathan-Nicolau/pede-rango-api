package com.nathannicolau.pederangoapi.dto.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardDTO(
    String periodo,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    long totalPedidos,
    long pedidosEntregues,
    long pedidosCancelados,
    long pedidosEmAndamento,
    BigDecimal faturamentoTotal,
    BigDecimal ticketMedio,
    Double tempoMedioEntregaMinutos,
    Double notaMediaAvaliacoes,
    long totalAvaliacoes,
    long totalEntregadoresAtivos,
    long totalClientes,
    List<TopProdutoDTO> topProdutos,
    List<DistribuicaoPagamentoDTO> distribuicaoPagamento,
    List<DistribuicaoStatusDTO> distribuicaoStatus,
    List<FaturamentoDiaDTO> faturamentoPorDia
) {}
