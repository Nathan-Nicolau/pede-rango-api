package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // 1. Buscar cliente pelo ID do usuário associado
    Optional<Cliente> findByUsuarioId(Long usuarioId);

    // 2. Buscar cliente pelo e-mail do usuário associado
    Optional<Cliente> findByUsuarioEmail(String email);

    // 3. Buscar cliente pelo CPF
    Optional<Cliente> findByCpf(String cpf);

    // 4. Listar clientes ativos paginados
    Page<Cliente> findByAtivoTrue(Pageable pageable);
}
