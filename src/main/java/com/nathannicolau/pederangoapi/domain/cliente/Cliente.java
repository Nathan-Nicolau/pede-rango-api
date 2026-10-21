package com.nathannicolau.pederangoapi.domain.cliente;

import com.nathannicolau.pederangoapi.domain.usuario.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade de melhoria, para relacionar o cliente a um usuário, permitindo que ele use a aplicação
 * criando e consultando seus pedidos
 * */

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(length = 20)
    private String cpf;

    @Column(length = 30)
    private String telefone;

    @Column(name = "endereco_padrao", length = 300)
    private String enderecoPadrao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public Cliente() {}

    public Cliente(Usuario usuario, String cpf, String telefone, String enderecoPadrao) {
        this.usuario = usuario;
        this.cpf = cpf;
        this.telefone = telefone;
        this.enderecoPadrao = enderecoPadrao;
        this.ativo = true;
    }

    @PrePersist
    protected void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    protected void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEnderecoPadrao() { return enderecoPadrao; }
    public void setEnderecoPadrao(String enderecoPadrao) { this.enderecoPadrao = enderecoPadrao; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
