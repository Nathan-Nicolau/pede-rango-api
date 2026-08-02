package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.pedido.Pedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    // 1. Listar pedidos por um status específico
    List<Pedido> findByStatusOrderByCriadoEmDesc(StatusPedido status);

    // 2. Listar pedidos por múltiplos status (ex: RECEBIDO, EM_PREPARO, SAIU_PARA_ENTREGA)
    List<Pedido> findByStatusInOrderByCriadoEmDesc(List<StatusPedido> statusList);

    // 3. Listar apenas pedidos VÁLIDOS (que NÃO foram cancelados)
    List<Pedido> findByStatusNotOrderByCriadoEmDesc(StatusPedido status);

    // 4. Listar pedidos entre duas datas (período de tempo)
    List<Pedido> findByCriadoEmBetweenOrderByCriadoEmDesc(LocalDateTime inicio, LocalDateTime fim);

    // 5. Listar pedidos VÁLIDOS (não cancelados) criados em um período de tempo
    List<Pedido> findByStatusNotAndCriadoEmBetweenOrderByCriadoEmDesc(
        StatusPedido status, LocalDateTime inicio, LocalDateTime fim
    );

    // 6. Listar pedidos associados a um entregador
    List<Pedido> findByEntregadorIdOrderByCriadoEmDesc(Long entregadorId);

    // 7. Listar pedidos associados a um cliente (meus pedidos web)
    List<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    Page<Pedido> findByClienteIdOrderByCriadoEmDesc(Long clienteId, Pageable pageable);
    Page<Pedido> findByClienteUsuarioEmailOrderByCriadoEmDesc(String email, Pageable pageable);

    // 8. Consulta JPQL Personalizada Combinada Paginada (Dashboard / Relatórios)
    @Query("""
        SELECT p FROM Pedido p
        WHERE (:somenteValidos = false OR p.status <> com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido.CANCELADO)
          AND (:status IS NULL OR p.status = :status)
          AND (CAST(:inicio AS timestamp) IS NULL OR p.criadoEm >= :inicio)
          AND (CAST(:fim AS timestamp) IS NULL OR p.criadoEm <= :fim)
        """)
    Page<Pedido> buscarComFiltrosAvancadosPaginado(
        @Param("somenteValidos") Boolean somenteValidos,
        @Param("status") StatusPedido status,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim,
        Pageable pageable
    );

    @Query("""
        SELECT p FROM Pedido p
        WHERE (:somenteValidos = false OR p.status <> com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido.CANCELADO)
          AND (:status IS NULL OR p.status = :status)
          AND (CAST(:inicio AS timestamp) IS NULL OR p.criadoEm >= :inicio)
          AND (CAST(:fim AS timestamp) IS NULL OR p.criadoEm <= :fim)
        ORDER BY p.criadoEm DESC
        """)
    List<Pedido> buscarComFiltrosAvancados(
        @Param("somenteValidos") Boolean somenteValidos,
        @Param("status") StatusPedido status,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}
