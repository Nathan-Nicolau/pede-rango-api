package com.nathannicolau.pederangoapi.controller;

import com.nathannicolau.pederangoapi.dto.configuracao.AbrirExpedienteDTO;
import com.nathannicolau.pederangoapi.dto.configuracao.AtualizarConfiguracaoDTO;
import com.nathannicolau.pederangoapi.dto.configuracao.ConfiguracaoEstabelecimentoDTO;
import com.nathannicolau.pederangoapi.service.ConfiguracaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/configuracoes", "/api/configuracoes"})
@RequiredArgsConstructor
@Tag(name = "Configurações do Estabelecimento", description = "Endpoints de gerenciamento operacional e parâmetros da loja")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    @GetMapping
    @Operation(summary = "Obter dados operacionais e configurações da loja")
    public ResponseEntity<ConfiguracaoEstabelecimentoDTO> obter() {
        return ResponseEntity.ok(configuracaoService.obterDTO());
    }

    @PostMapping("/abrir-expediente")
    @Operation(summary = "Iniciar dia de trabalho / Abrir operações do estabelecimento")
    public ResponseEntity<ConfiguracaoEstabelecimentoDTO> abrirExpediente(@RequestBody(required = false) AbrirExpedienteDTO dto) {
        return ResponseEntity.ok(configuracaoService.abrirExpediente(dto));
    }

    @PostMapping("/fechar-expediente")
    @Operation(summary = "Encerrar expediente / Fechar loja para novos pedidos")
    public ResponseEntity<ConfiguracaoEstabelecimentoDTO> fecharExpediente() {
        return ResponseEntity.ok(configuracaoService.fecharExpediente());
    }

    @PostMapping("/alternar-status")
    @Operation(summary = "Alternar loja entre Aberta e Fechada")
    public ResponseEntity<ConfiguracaoEstabelecimentoDTO> alternarStatus() {
        return ResponseEntity.ok(configuracaoService.alternarStatus());
    }

    @PutMapping
    @Operation(summary = "Atualizar taxas, tempo estimado e dados do estabelecimento")
    public ResponseEntity<ConfiguracaoEstabelecimentoDTO> atualizar(@RequestBody @Valid AtualizarConfiguracaoDTO dto) {
        return ResponseEntity.ok(configuracaoService.atualizarConfiguracoes(dto));
    }
}
