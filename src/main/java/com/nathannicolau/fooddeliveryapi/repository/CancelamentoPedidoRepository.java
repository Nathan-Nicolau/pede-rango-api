package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.pedido.CancelamentoPedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.MotivoCancelamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CancelamentoPedidoRepository extends JpaRepository<CancelamentoPedido, Long> {

    // 1. Buscar o cancelamento associado ao ID interno do pedido
    Optional<CancelamentoPedido> findByPedidoId(Long pedidoId);

    // 2. Buscar o cancelamento associado ao código externo (UUID/PED-...) do pedido
    Optional<CancelamentoPedido> findByPedidoCodigo(String codigo);

    // 3. Relatório: Listar todos os cancelamentos por um motivo específico (ex: DESISTENCIA_CLIENTE, FALTA_DE_ESTOQUE)
    List<CancelamentoPedido> findByMotivoOrderByCanceladoEmDesc(MotivoCancelamento motivo);

    // 4. Relatório: Listar cancelamentos ocorridos dentro de um período de tempo
    List<CancelamentoPedido> findByCanceladoEmBetweenOrderByCanceladoEmDesc(LocalDateTime inicio, LocalDateTime fim);
}
