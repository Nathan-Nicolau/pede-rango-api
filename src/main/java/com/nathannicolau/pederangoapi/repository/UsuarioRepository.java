package com.nathannicolau.pederangoapi.repository;

import com.nathannicolau.pederangoapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.pederangoapi.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 1. Buscar usuário pelo email único (Autenticação / Login)
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailIgnoreCase(String email);

    // 2. Verificar existência por email (Registro de Novos Usuários)
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);

    // 3. Listar usuários ATIVOS por Perfil (CLIENTE, ENTREGADOR, RESTAURANTE, ADMIN)
    List<Usuario> findByPerfilAndAtivoTrue(PerfilUsuario perfil);
    Page<Usuario> findByPerfilAndAtivoTrue(PerfilUsuario perfil, Pageable pageable);

    // 4. Listar todos os usuários ATIVOS
    List<Usuario> findByAtivoTrue();
    Page<Usuario> findByAtivoTrue(Pageable pageable);

    // 5. Buscar usuários por parte do nome (Filtro / Busca de clientes ou colaboradores)
    List<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    Page<Usuario> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);
}
