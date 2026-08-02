package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.pedido.*;
import com.nathannicolau.fooddeliveryapi.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public ResponseEntity<Page<RespostaPedidoDTO>> listarTodosPaginado(
        @RequestParam(required = false) Boolean somenteValidos,
        @RequestParam(required = false) StatusPedido status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<RespostaPedidoDTO> pedidos = pedidoService.listarTodosPaginado(somenteValidos, status, inicio, fim, pageable);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<Page<RespostaPedidoDTO>> meusPedidos(
        Authentication authentication,
        @PageableDefault(size = 10, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        String email = "";
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof Usuario usuario) {
                email = usuario.getEmail();
            } else {
                email = authentication.getName();
            }
        }
        Page<RespostaPedidoDTO> pedidos = pedidoService.listarPorClientePaginado(email, pageable);
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
