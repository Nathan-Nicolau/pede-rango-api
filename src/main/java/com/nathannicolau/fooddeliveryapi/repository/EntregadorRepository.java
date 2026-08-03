package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

    // 1. Buscar entregador pelo ID do usuário associado
    Optional<Entregador> findByUsuarioId(Long usuarioId);

    // 2. Buscar entregador pelo email do usuário associado
    Optional<Entregador> findByUsuarioEmail(String email);

    // 3. Buscar entregadores DISPONÍVEIS e ATIVOS (prontos para receber atribuição de pedido)
    List<Entregador> findByDisponivelTrueAndAtivoTrue();
    Page<Entregador> findByDisponivelTrueAndAtivoTrue(Pageable pageable);

    // 4. Listar todos os entregadores ATIVOS cadastrados
    List<Entregador> findByAtivoTrue();
    Page<Entregador> findByAtivoTrue(Pageable pageable);

    // 5. Filtrar entregadores por tipo de veículo (ex: Moto, Carro, Bicicleta)
    List<Entregador> findByVeiculoContainingIgnoreCaseAndAtivoTrue(String veiculo);

    // 6. Contar quantos entregadores estão online/disponíveis no momento
    long countByDisponivelTrueAndAtivoTrue();

    // 7. Checar telefone normalizado
    @Query("SELECT COUNT(e) > 0 FROM Entregador e WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(e.telefone, ''), '(', ''), ')', ''), '-', ''), ' ', ''), '+', '') = :telLimpo")
    boolean existsByTelefoneNormalizado(@Param("telLimpo") String telLimpo);
}
