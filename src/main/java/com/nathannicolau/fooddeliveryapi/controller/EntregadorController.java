package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.dto.entregador.CriarEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.dto.entregador.RespostaEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.service.EntregadorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entregadores")
@CrossOrigin(origins = "*")
public class EntregadorController {

    private final EntregadorService entregadorService;

    public EntregadorController(EntregadorService entregadorService) {
        this.entregadorService = entregadorService;
    }

    @PostMapping
    public ResponseEntity<RespostaEntregadorDTO> cadastrar(@RequestBody @Valid CriarEntregadorDTO dto) {
        RespostaEntregadorDTO resposta = entregadorService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<RespostaEntregadorDTO>> listarTodos(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RespostaEntregadorDTO> entregadores = entregadorService.listarTodosPaginado(pageable);
        return ResponseEntity.ok(entregadores);
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<RespostaEntregadorDTO>> listarDisponiveis() {
        List<RespostaEntregadorDTO> disponiveis = entregadorService.listarDisponiveis();
        return ResponseEntity.ok(disponiveis);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaEntregadorDTO> buscarPorId(@PathVariable Long id) {
        RespostaEntregadorDTO entregador = entregadorService.buscarPorId(id);
        return ResponseEntity.ok(entregador);
    }

    @PatchMapping("/{id}/disponibilidade")
    public ResponseEntity<RespostaEntregadorDTO> alternarDisponibilidade(
        @PathVariable Long id,
        @RequestParam boolean disponivel
    ) {
        RespostaEntregadorDTO resposta = entregadorService.alternarDisponibilidade(id, disponivel);
        return ResponseEntity.ok(resposta);
    }
}
