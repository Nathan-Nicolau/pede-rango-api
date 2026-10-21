package com.nathannicolau.pederangoapi.repository;

import com.nathannicolau.pederangoapi.domain.pedido.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    // 1. Buscar todos os itens de um pedido pelo ID interno do pedido
    List<ItemPedido> findByPedidoId(Long pedidoId);

    // 2. Buscar todos os itens de um pedido pelo código externo (UUID/PED-...) do pedido
    List<ItemPedido> findByPedidoCodigo(String codigo);

    // 3. Contar quantidade total de itens em um pedido
    @Query("SELECT SUM(i.quantidade) FROM ItemPedido i WHERE i.pedido.id = :pedidoId")
    Integer calcularTotalItensDoPedido(@Param("pedidoId") Long pedidoId);
}
