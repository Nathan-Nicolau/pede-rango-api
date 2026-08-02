package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.produto.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // 1. Listar todos os produtos ATIVOS no cardápio
    List<Produto> findByAtivoTrue();
    Page<Produto> findByAtivoTrue(Pageable pageable);

    // 2. Listar produtos por Categoria (ex: Lanches, Bebidas, Sobremesas)
    List<Produto> findByCategoriaAndAtivoTrue(String categoria);
    Page<Produto> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);

    // 3. Buscar produtos por parte do nome (Filtro de Busca)
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);
    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);
}
