package com.nathannicolau.pederangoapi.repository;

import com.nathannicolau.pederangoapi.domain.pedido.CancelamentoPedido;
import com.nathannicolau.pederangoapi.domain.pedido.MotivoCancelamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancelamentoPedidoRepository extends JpaRepository<CancelamentoPedido, Long> {

    // 1. Buscar o cancelamento associado ao ID interno do pedido
    Optional<CancelamentoPedido> findByPedidoId(Long pedidoId);

    // 2. Buscar o cancelamento associado ao código externo (PED-...) do pedido
    Optional<CancelamentoPedido> findByPedidoCodigo(String codigo);

    // 3. Relatório: Listar todos os cancelamentos por motivo (Paginado)
    List<CancelamentoPedido> findByMotivoOrderByCanceladoEmDesc(MotivoCancelamento motivo);
    Page<CancelamentoPedido> findByMotivoOrderByCanceladoEmDesc(MotivoCancelamento motivo, Pageable pageable);

    // 4. Relatório: Listar cancelamentos ocorridos dentro de um período de tempo (Paginado)
    List<CancelamentoPedido> findByCanceladoEmBetweenOrderByCanceladoEmDesc(LocalDateTime inicio, LocalDateTime fim);
    Page<CancelamentoPedido> findByCanceladoEmBetweenOrderByCanceladoEmDesc(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    // 5. Listar todos os cancelamentos paginados
    Page<CancelamentoPedido> findAllByOrderByCanceladoEmDesc(Pageable pageable);
}
