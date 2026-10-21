package com.nathannicolau.pederangoapi.domain.configuracao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracao_estabelecimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ConfiguracaoEstabelecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_loja", nullable = false, length = 100)
    private String nomeLoja = "Pede Rango Delivery";

    @Column(name = "telefone_contato", length = 30)
    private String telefoneContato = "(11) 98765-4321";

    @Column(name = "chave_pix", length = 100)
    private String chavePix = "financeiro@pederango.com";

    @Column(name = "taxa_entrega_padrao", nullable = false)
    private BigDecimal taxaEntregaPadrao = new BigDecimal("5.00");

    @Column(name = "tempo_estimado_min_minutos", nullable = false)
    private Integer tempoEstimadoMinMinutos = 30;

    @Column(name = "tempo_estimado_max_minutos", nullable = false)
    private Integer tempoEstimadoMaxMinutos = 45;

    @Column(name = "loja_aberta", nullable = false)
    private Boolean lojaAberta = false;

    @Column(name = "data_expediente")
    private LocalDate dataExpediente;

    @Column(name = "aberto_em")
    private LocalDateTime abertoEm;

    @Column(name = "fechado_em")
    private LocalDateTime fechadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    protected void aoSalvar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean isOperacionalmenteAbertaHoje() {
        if (!Boolean.TRUE.equals(this.lojaAberta)) {
            return false;
        }
        if (this.dataExpediente == null) {
            return false;
        }
        return this.dataExpediente.equals(LocalDate.now());
    }

    public boolean isExpedienteIniciadoHoje() {
        if (this.dataExpediente == null) {
            return false;
        }
        return this.dataExpediente.equals(LocalDate.now());
    }
}
