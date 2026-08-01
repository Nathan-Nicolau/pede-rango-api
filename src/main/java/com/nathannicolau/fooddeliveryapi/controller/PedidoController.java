package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import com.nathannicolau.fooddeliveryapi.dto.pedido.*;
import com.nathannicolau.fooddeliveryapi.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<RespostaPedidoDTO> criarPedido(@RequestBody @Valid CriarPedidoDTO dto) {
        RespostaPedidoDTO resposta = pedidoService.criarPedido(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<List<RespostaPedidoDTO>> listarTodos(
        @RequestParam(required = false) Boolean somenteValidos,
        @RequestParam(required = false) StatusPedido status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        List<RespostaPedidoDTO> pedidos = pedidoService.listarTodos(somenteValidos, status, inicio, fim);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{termo}")
    public ResponseEntity<RespostaPedidoDTO> buscarPorCodigoOuId(@PathVariable String termo) {
        RespostaPedidoDTO pedido = pedidoService.buscarPorCodigoOuId(termo);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{termo}/status")
    public ResponseEntity<RespostaPedidoDTO> atualizarStatus(
        @PathVariable String termo,
        @RequestBody @Valid AtualizarStatusPedidoDTO dto
    ) {
        RespostaPedidoDTO resposta = pedidoService.atualizarStatus(termo, dto.status());
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/{termo}/atribuir-entregador/{entregadorId}")
    public ResponseEntity<RespostaPedidoDTO> atribuirEntregador(
        @PathVariable String termo,
        @PathVariable Long entregadorId
    ) {
        RespostaPedidoDTO resposta = pedidoService.atribuirEntregador(termo, entregadorId);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/{termo}/cancelar")
    public ResponseEntity<RespostaPedidoDTO> cancelarPedido(
        @PathVariable String termo,
        @RequestBody @Valid CancelarPedidoDTO dto
    ) {
        RespostaPedidoDTO resposta = pedidoService.cancelarPedido(termo, dto);
        return ResponseEntity.ok(resposta);
    }
}
