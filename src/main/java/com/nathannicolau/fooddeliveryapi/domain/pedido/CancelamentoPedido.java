package com.nathannicolau.fooddeliveryapi.domain.pedido;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Essa entidade foi projetado como melhoria. A ideia é ter uma tabela somente para registros de informações
 * dos pedidos cancelados, como forma de rastrear os motivos e projetar melhorias
 * */

@Entity
@Table(name = "cancelamentos_pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CancelamentoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    @JsonIgnore
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo", nullable = false)
    private MotivoCancelamento motivo;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "cancelado_em", nullable = false)
    private LocalDateTime canceladoEm;

    @PrePersist
    protected void aoCriar() {
        if (this.canceladoEm == null) {
            this.canceladoEm = LocalDateTime.now();
        }
    }
}
