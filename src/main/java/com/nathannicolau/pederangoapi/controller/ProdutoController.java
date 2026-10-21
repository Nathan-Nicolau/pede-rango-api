package com.nathannicolau.pederangoapi.controller;

import com.nathannicolau.pederangoapi.dto.produto.CriarProdutoDTO;
import com.nathannicolau.pederangoapi.dto.produto.RespostaProdutoDTO;
import com.nathannicolau.pederangoapi.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<RespostaProdutoDTO> cadastrar(@RequestBody @Valid CriarProdutoDTO dto) {
        RespostaProdutoDTO resposta = produtoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaProdutoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid CriarProdutoDTO dto) {
        RespostaProdutoDTO resposta = produtoService.atualizar(id, dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<RespostaProdutoDTO>> listarTodos(
        @RequestParam(required = false) String categoria,
        @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RespostaProdutoDTO> produtos = produtoService.listarTodosPaginado(categoria, pageable);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaProdutoDTO> buscarPorId(@PathVariable Long id) {
        RespostaProdutoDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RespostaProdutoDTO> atualizarStatus(
        @PathVariable Long id,
        @RequestParam boolean ativo
    ) {
        RespostaProdutoDTO resposta = produtoService.atualizarStatus(id, ativo);
        return ResponseEntity.ok(resposta);
    }
}
