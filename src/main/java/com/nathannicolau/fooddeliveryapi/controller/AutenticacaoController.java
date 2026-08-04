package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.dto.auth.LoginDTO;
import com.nathannicolau.fooddeliveryapi.dto.auth.RegistroDTO;
import com.nathannicolau.fooddeliveryapi.dto.auth.RespostaTokenDTO;
import com.nathannicolau.fooddeliveryapi.service.AutenticacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    public AutenticacaoController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping({"/register", "/registrar"})
    public ResponseEntity<RespostaTokenDTO> registrar(@RequestBody @Valid RegistroDTO dto) {
        RespostaTokenDTO resposta = autenticacaoService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    public ResponseEntity<RespostaTokenDTO> login(@RequestBody @Valid LoginDTO dto) {
        RespostaTokenDTO resposta = autenticacaoService.autenticar(dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/checar-email")
    public ResponseEntity<Map<String, Boolean>> checarEmail(@RequestParam String email) {
        boolean existe = autenticacaoService.existeEmail(email);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @GetMapping("/checar-cpf")
    public ResponseEntity<Map<String, Boolean>> checarCpf(@RequestParam String cpf) {
        boolean existe = autenticacaoService.existeCpf(cpf);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    @GetMapping("/checar-telefone")
    public ResponseEntity<Map<String, Boolean>> checarTelefone(@RequestParam String telefone) {
        boolean existe = autenticacaoService.existeTelefone(telefone);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
}
