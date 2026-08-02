package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.dto.cliente.CriarClienteDTO;
import com.nathannicolau.fooddeliveryapi.dto.cliente.RespostaClienteDTO;
import com.nathannicolau.fooddeliveryapi.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<RespostaClienteDTO> cadastrar(@RequestBody @Valid CriarClienteDTO dto) {
        RespostaClienteDTO resposta = clienteService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    public ResponseEntity<Page<RespostaClienteDTO>> listarTodos(
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RespostaClienteDTO> clientes = clienteService.listarTodosPaginado(pageable);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaClienteDTO> buscarPorId(@PathVariable Long id) {
        RespostaClienteDTO cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(cliente);
    }
}
