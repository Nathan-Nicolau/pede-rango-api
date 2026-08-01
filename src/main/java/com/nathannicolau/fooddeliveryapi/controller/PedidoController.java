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

    @GetMapping({"", "/listar-pedidos"})
    public ResponseEntity<List<RespostaPedidoDTO>> listarTodos(
        @RequestParam(required = false) Boolean somenteValidos,
        @RequestParam(required = false) StatusPedido status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        List<RespostaPedidoDTO> pedidos = pedidoService.listarTodos(somenteValidos, status, inicio, fim);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{codigoOuId}")
    public ResponseEntity<RespostaPedidoDTO> buscarPorCodigoOuId(@PathVariable String codigoOuId) {
        RespostaPedidoDTO pedido = pedidoService.buscarPorCodigoOuId(codigoOuId);
        return ResponseEntity.ok(pedido);
    }

    @PatchMapping("/{codigoOuId}/status")
    public ResponseEntity<RespostaPedidoDTO> atualizarStatus(
        @PathVariable String codigoOuId,
        @RequestBody @Valid AtualizarStatusPedidoDTO dto
    ) {
        RespostaPedidoDTO resposta = pedidoService.atualizarStatus(codigoOuId, dto.status());
        return ResponseEntity.ok(resposta);
    }

    @PatchMapping("/{codigoOuId}/atribuir-entregador/{entregadorId}")
    public ResponseEntity<RespostaPedidoDTO> atribuirEntregador(
        @PathVariable String codigoOuId,
        @PathVariable Long entregadorId
    ) {
        RespostaPedidoDTO resposta = pedidoService.atribuirEntregador(codigoOuId, entregadorId);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/{codigoOuId}/cancelar")
    public ResponseEntity<RespostaPedidoDTO> cancelarPedido(
        @PathVariable String codigoOuId,
        @RequestBody @Valid CancelarPedidoDTO dto
    ) {
        RespostaPedidoDTO resposta = pedidoService.cancelarPedido(codigoOuId, dto);
        return ResponseEntity.ok(resposta);
    }
}
