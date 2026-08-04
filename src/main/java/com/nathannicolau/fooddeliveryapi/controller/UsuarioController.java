package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.dto.auth.RegistroDTO;
import com.nathannicolau.fooddeliveryapi.dto.usuario.RespostaUsuarioDTO;
import com.nathannicolau.fooddeliveryapi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<Page<RespostaUsuarioDTO>> listarTodos(
        @RequestParam(required = false) PerfilUsuario perfil,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<RespostaUsuarioDTO> usuarios;
        if (perfil != null) {
            usuarios = usuarioService.listarPorPerfilPaginado(perfil, pageable);
        } else {
            usuarios = usuarioService.listarTodosPaginado(pageable);
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespostaUsuarioDTO> buscarPorId(@PathVariable Long id) {
        RespostaUsuarioDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint exclusivo para administradores criarem usuários com qualquer perfil
     * (ADMIN, ENTREGADOR, RESTAURANTE). Não passa pela regra do /auth/register
     * que força o perfil CLIENTE.
     */
    @PostMapping
    public ResponseEntity<RespostaUsuarioDTO> cadastrar(@RequestBody @Valid RegistroDTO dto) {
        var usuario = usuarioService.cadastrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(RespostaUsuarioDTO.daEntidade(usuario));
    }
}
