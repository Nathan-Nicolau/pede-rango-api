package com.nathannicolau.fooddeliveryapi.domain.entregador;

import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "entregadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "veiculo", nullable = false)
    private String veiculo;

    @Column(name = "placa")
    private String placa;

    @Column(name = "disponivel", nullable = false)
    private Boolean disponivel = true;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public Entregador(Usuario usuario, String telefone, String veiculo, String placa) {
        this.usuario = usuario;
        if (this.usuario != null) {
            this.usuario.setPerfil(PerfilUsuario.ENTREGADOR);
        }
        this.telefone = telefone;
        this.veiculo = veiculo;
        this.placa = placa;
        this.disponivel = true;
        this.ativo = true;
    }

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.disponivel == null) {
            this.disponivel = true;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
        if (this.usuario != null) {
            this.usuario.setPerfil(PerfilUsuario.ENTREGADOR);
        }
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
