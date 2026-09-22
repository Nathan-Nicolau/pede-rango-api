package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.avaliacao.AvaliacaoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoPedidoRepository extends JpaRepository<AvaliacaoPedido, Long> {
    Optional<AvaliacaoPedido> findByPedidoId(Long pedidoId);
    Optional<AvaliacaoPedido> findByPedidoCodigo(String codigo);

    @Query("SELECT AVG(a.nota) FROM AvaliacaoPedido a")
    Double calcularMediaAvaliacoes();

    @Query("SELECT COUNT(a) FROM AvaliacaoPedido a")
    Long contarTotalAvaliacoes();
}
