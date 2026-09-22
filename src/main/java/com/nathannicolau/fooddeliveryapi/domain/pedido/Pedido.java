package com.nathannicolau.fooddeliveryapi.domain.pedido;

import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, updatable = false, length = 30)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "endereco_entrega", nullable = false)
    private String enderecoEntrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemPedido> itens = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private CancelamentoPedido cancelamento;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private com.nathannicolau.fooddeliveryapi.domain.avaliacao.AvaliacaoPedido avaliacao;

    @Column(name = "taxa_entrega", nullable = false)
    private BigDecimal taxaEntrega = BigDecimal.ZERO;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false, length = 50)
    private FormaPagamento formaPagamento = FormaPagamento.DINHEIRO;

    @Column(name = "troco_para")
    private BigDecimal trocoPara;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusPedido.RECEBIDO;
        }
    }

    public void gerarCodigoSequencial(long sequencia) {
        String dataStr = (this.criadoEm != null ? this.criadoEm : LocalDateTime.now())
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.codigo = String.format("PED-%s-%04d", dataStr, sequencia);
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
        if (this.status == StatusPedido.ENTREGUE && this.dataEntrega == null) {
            this.dataEntrega = LocalDateTime.now();
        }
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void cancelar(MotivoCancelamento motivo, String observacoes) {
        this.status = StatusPedido.CANCELADO;
        CancelamentoPedido cancelamentoInfo = new CancelamentoPedido();
        cancelamentoInfo.setPedido(this);
        cancelamentoInfo.setMotivo(motivo);
        cancelamentoInfo.setObservacoes(observacoes);
        this.cancelamento = cancelamentoInfo;
    }
}
