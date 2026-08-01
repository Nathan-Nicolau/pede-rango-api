package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    // 4. Listar todos os entregadores ATIVOS cadastrados
    List<Entregador> findByAtivoTrue();

    // 5. Filtrar entregadores por tipo de veículo (ex: Moto, Carro, Bicicleta)
    List<Entregador> findByVeiculoContainingIgnoreCaseAndAtivoTrue(String veiculo);

    // 6. Contar quantos entregadores estão online/disponíveis no momento
    long countByDisponivelTrueAndAtivoTrue();
}
