package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // 1. Buscar cliente pelo ID do usuário associado
    Optional<Cliente> findByUsuarioId(Long usuarioId);

    // Verificação de existência por ID do usuário
    boolean existsByUsuarioId(Long usuarioId);

    // 2. Buscar cliente pelo e-mail do usuário associado
    Optional<Cliente> findByUsuarioEmail(String email);

    // 3. Buscar cliente pelo CPF
    Optional<Cliente> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByTelefone(String telefone);

    // 4. Verificação normalizada de CPF e Telefone ignorando caracteres de formatação
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE REPLACE(REPLACE(REPLACE(COALESCE(c.cpf, ''), '.', ''), '-', ''), ' ', '') = :cpfLimpo")
    boolean existsByCpfNormalizado(@Param("cpfLimpo") String cpfLimpo);

    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(COALESCE(c.telefone, ''), '(', ''), ')', ''), '-', ''), ' ', ''), '+', '') = :telLimpo")
    boolean existsByTelefoneNormalizado(@Param("telLimpo") String telLimpo);

    // 5. Listar clientes ativos paginados
    Page<Cliente> findByAtivoTrue(Pageable pageable);
}
