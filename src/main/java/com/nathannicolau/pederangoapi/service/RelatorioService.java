package com.nathannicolau.pederangoapi.service;

import com.nathannicolau.pederangoapi.domain.pedido.FormaPagamento;
import com.nathannicolau.pederangoapi.domain.pedido.ItemPedido;
import com.nathannicolau.pederangoapi.domain.pedido.Pedido;
import com.nathannicolau.pederangoapi.domain.pedido.StatusPedido;
import com.nathannicolau.pederangoapi.dto.relatorio.*;
import com.nathannicolau.pederangoapi.repository.ClienteRepository;
import com.nathannicolau.pederangoapi.repository.EntregadorRepository;
import com.nathannicolau.pederangoapi.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EntregadorRepository entregadorRepository;
    private final com.nathannicolau.pederangoapi.repository.AvaliacaoPedidoRepository avaliacaoPedidoRepository;

    public RelatorioService(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            EntregadorRepository entregadorRepository,
                            com.nathannicolau.pederangoapi.repository.AvaliacaoPedidoRepository avaliacaoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.entregadorRepository = entregadorRepository;
        this.avaliacaoPedidoRepository = avaliacaoPedidoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardDTO gerarDashboard(String tipoPeriodo, LocalDateTime inicioCustom, LocalDateTime fimCustom) {
        String periodo = tipoPeriodo != null ? tipoPeriodo.toUpperCase().trim() : "SEMANA";
        LocalDateTime inicio;
        LocalDateTime fim;

        LocalDate hoje = LocalDate.now();

        switch (periodo) {
            case "HOJE", "DIA" -> {
                inicio = hoje.atStartOfDay();
                fim = hoje.atTime(LocalTime.MAX);
            }
            case "ONTEM" -> {
                LocalDate ontem = hoje.minusDays(1);
                inicio = ontem.atStartOfDay();
                fim = ontem.atTime(LocalTime.MAX);
            }
            case "QUINZENA", "15_DIAS" -> {
                inicio = hoje.minusDays(14).atStartOfDay();
                fim = hoje.atTime(LocalTime.MAX);
            }
            case "MES", "30_DIAS" -> {
                inicio = hoje.withDayOfMonth(1).atStartOfDay();
                fim = hoje.atTime(LocalTime.MAX);
            }
            case "CUSTOM" -> {
                inicio = inicioCustom != null ? inicioCustom : hoje.minusDays(6).atStartOfDay();
                fim = fimCustom != null ? fimCustom : hoje.atTime(LocalTime.MAX);
            }
            default -> { // SEMANA / 7_DIAS
                periodo = "SEMANA";
                inicio = hoje.minusDays(6).atStartOfDay();
                fim = hoje.atTime(LocalTime.MAX);
            }
        }

        List<Pedido> pedidos = pedidoRepository.buscarComFiltrosAvancados(false, null, inicio, fim);

        long totalPedidos = pedidos.size();
        long pedidosEntregues = pedidos.stream().filter(p -> p.getStatus() == StatusPedido.ENTREGUE).count();
        long pedidosCancelados = pedidos.stream().filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();
        long pedidosEmAndamento = totalPedidos - pedidosEntregues - pedidosCancelados;

        BigDecimal faturamentoTotal = pedidos.stream()
            .filter(p -> p.getStatus() == StatusPedido.ENTREGUE)
            .map(Pedido::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ticketMedio = pedidosEntregues > 0
            ? faturamentoTotal.divide(BigDecimal.valueOf(pedidosEntregues), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Tempo médio de entrega (em minutos) para pedidos com dataEntrega registrada
        List<Long> minutosEntregas = pedidos.stream()
            .filter(p -> p.getStatus() == StatusPedido.ENTREGUE && p.getDataEntrega() != null && p.getCriadoEm() != null)
            .map(p -> Duration.between(p.getCriadoEm(), p.getDataEntrega()).toMinutes())
            .filter(minutos -> minutos > 0 && minutos < 600)
            .toList();

        Double tempoMedioEntrega = minutosEntregas.isEmpty() ? 0.0 :
            minutosEntregas.stream().mapToLong(Long::longValue).average().orElse(0.0);

        long totalEntregadoresAtivos = entregadorRepository.count();
        long totalClientes = clienteRepository.count();

        // 1. Top Produtos Mais Vendidos
        Map<String, TopProdutoAccumulator> produtosAcumulados = new HashMap<>();
        for (Pedido p : pedidos) {
            if (p.getStatus() != StatusPedido.CANCELADO && p.getItens() != null) {
                for (ItemPedido item : p.getItens()) {
                    String nome = item.getNome();
                    String categoria = (item.getProduto() != null && item.getProduto().getCategoria() != null)
                        ? item.getProduto().getCategoria() : "Geral";
                    Long produtoId = item.getProduto() != null ? item.getProduto().getId() : null;
                    int qtd = item.getQuantidade() != null ? item.getQuantidade() : 1;
                    BigDecimal subtotal = item.getPrecoUnitario() != null
                        ? item.getPrecoUnitario().multiply(BigDecimal.valueOf(qtd)) : BigDecimal.ZERO;

                    produtosAcumulados.computeIfAbsent(nome, k -> new TopProdutoAccumulator(produtoId, nome, categoria))
                        .adicionar(qtd, subtotal);
                }
            }
        }

        List<TopProdutoDTO> topProdutos = produtosAcumulados.values().stream()
            .sorted(Comparator.comparingInt(TopProdutoAccumulator::getQuantidade).reversed())
            .limit(5)
            .map(acc -> new TopProdutoDTO(acc.getProdutoId(), acc.getNome(), acc.getCategoria(), acc.getQuantidade(), acc.getTotal()))
            .toList();

        // 2. Distribuição por Forma de Pagamento
        Map<FormaPagamento, Long> contagemPagamento = pedidos.stream()
            .collect(Collectors.groupingBy(p -> p.getFormaPagamento() != null ? p.getFormaPagamento() : FormaPagamento.DINHEIRO, Collectors.counting()));

        Map<FormaPagamento, BigDecimal> totalPorPagamento = pedidos.stream()
            .collect(Collectors.groupingBy(
                p -> p.getFormaPagamento() != null ? p.getFormaPagamento() : FormaPagamento.DINHEIRO,
                Collectors.reducing(BigDecimal.ZERO, Pedido::getValorTotal, BigDecimal::add)
            ));

        List<DistribuicaoPagamentoDTO> distribuicaoPagamento = Arrays.stream(FormaPagamento.values())
            .map(forma -> new DistribuicaoPagamentoDTO(
                forma.name(),
                formatarLabelPagamento(forma),
                contagemPagamento.getOrDefault(forma, 0L),
                totalPorPagamento.getOrDefault(forma, BigDecimal.ZERO)
            ))
            .filter(d -> d.quantidade() > 0 || totalPedidos == 0)
            .toList();

        // 3. Distribuição por Status
        Map<StatusPedido, Long> contagemStatus = pedidos.stream()
            .collect(Collectors.groupingBy(Pedido::getStatus, Collectors.counting()));

        List<DistribuicaoStatusDTO> distribuicaoStatus = Arrays.stream(StatusPedido.values())
            .map(status -> new DistribuicaoStatusDTO(
                status.name(),
                formatarLabelStatus(status),
                contagemStatus.getOrDefault(status, 0L)
            ))
            .toList();

        // 4. Faturamento por Dia no Período
        List<FaturamentoDiaDTO> faturamentoPorDia = new ArrayList<>();
        LocalDate diaIterador = inicio.toLocalDate();
        LocalDate diaLimite = fim.toLocalDate();
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM");

        while (!diaIterador.isAfter(diaLimite)) {
            LocalDate dataAtual = diaIterador;
            List<Pedido> pedidosDoDia = pedidos.stream()
                .filter(p -> p.getCriadoEm() != null && p.getCriadoEm().toLocalDate().equals(dataAtual))
                .toList();

            BigDecimal fatDia = pedidosDoDia.stream()
                .filter(p -> p.getStatus() == StatusPedido.ENTREGUE)
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            String diaSemana = dataAtual.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
            faturamentoPorDia.add(new FaturamentoDiaDTO(
                dataAtual.format(formatadorData),
                diaSemana,
                fatDia,
                pedidosDoDia.size()
            ));

            diaIterador = diaIterador.plusDays(1);
        }

        Double notaMedia = avaliacaoPedidoRepository.calcularMediaAvaliacoes();
        Double notaMediaAvaliacoes = notaMedia != null ? Math.round(notaMedia * 10.0) / 10.0 : 0.0;
        Long totalAvaliacoes = avaliacaoPedidoRepository.contarTotalAvaliacoes();
        long totalAvaliacoesCount = totalAvaliacoes != null ? totalAvaliacoes : 0L;

        return new DashboardDTO(
            periodo,
            inicio,
            fim,
            totalPedidos,
            pedidosEntregues,
            pedidosCancelados,
            pedidosEmAndamento,
            faturamentoTotal,
            ticketMedio,
            tempoMedioEntrega,
            notaMediaAvaliacoes,
            totalAvaliacoesCount,
            totalEntregadoresAtivos,
            totalClientes,
            topProdutos,
            distribuicaoPagamento,
            distribuicaoStatus,
            faturamentoPorDia
        );
    }

    private String formatarLabelPagamento(FormaPagamento forma) {
        return switch (forma) {
            case DINHEIRO -> "Dinheiro";
            case PIX -> "PIX";
            case CARTAO_CREDITO -> "Cartão de Crédito";
            case CARTAO_DEBITO -> "Cartão de Débito";
        };
    }

    private String formatarLabelStatus(StatusPedido status) {
        return switch (status) {
            case RECEBIDO -> "Recebido";
            case EM_PREPARO -> "Em Preparo";
            case PRONTO_PARA_ENTREGA -> "Pronto p/ Entrega";
            case SAIU_PARA_ENTREGA -> "Saiu p/ Entrega";
            case ENTREGUE -> "Entregue";
            case CANCELADO -> "Cancelado";
        };
    }

    @Transactional(readOnly = true)
    public byte[] exportarCsv(String tipoPeriodo, LocalDateTime inicioCustom, LocalDateTime fimCustom) {
        DashboardDTO dashboard = gerarDashboard(tipoPeriodo, inicioCustom, fimCustom);
        List<Pedido> pedidos = pedidoRepository.buscarComFiltrosAvancados(false, null, dashboard.dataInicio(), dashboard.dataFim());

        StringBuilder csv = new StringBuilder();
        csv.append("\uFEFF"); // UTF-8 BOM
        csv.append("Código;Data Criação;Cliente;Telefone;Status;Forma Pagamento;Taxa Entrega (R$);Valor Total (R$);Entregador\n");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (Pedido p : pedidos) {
            String data = p.getCriadoEm() != null ? p.getCriadoEm().format(dtf) : "";
            String cliente = p.getNomeCliente() != null ? p.getNomeCliente().replace(";", ",") : "";
            String tel = (p.getCliente() != null && p.getCliente().getTelefone() != null) ? p.getCliente().getTelefone() : "";
            String status = formatarLabelStatus(p.getStatus());
            String forma = formatarLabelPagamento(p.getFormaPagamento());
            String taxa = p.getTaxaEntrega() != null ? String.format(Locale.US, "%.2f", p.getTaxaEntrega()) : "0.00";
            String total = p.getValorTotal() != null ? String.format(Locale.US, "%.2f", p.getValorTotal()) : "0.00";
            String entregador = (p.getEntregador() != null && p.getEntregador().getUsuario() != null)
                ? p.getEntregador().getUsuario().getNome().replace(";", ",") : "Não atribuído";

            csv.append(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s\n",
                p.getCodigo(), data, cliente, tel, status, forma, taxa, total, entregador
            ));
        }

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static class TopProdutoAccumulator {
        private final Long produtoId;
        private final String nome;
        private final String categoria;
        private int quantidade = 0;
        private BigDecimal total = BigDecimal.ZERO;

        public TopProdutoAccumulator(Long produtoId, String nome, String categoria) {
            this.produtoId = produtoId;
            this.nome = nome;
            this.categoria = categoria;
        }

        public void adicionar(int qtd, BigDecimal valor) {
            this.quantidade += qtd;
            this.total = this.total.add(valor);
        }

        public Long getProdutoId() { return produtoId; }
        public String getNome() { return nome; }
        public String getCategoria() { return categoria; }
        public int getQuantidade() { return quantidade; }
        public BigDecimal getTotal() { return total; }
    }
}
