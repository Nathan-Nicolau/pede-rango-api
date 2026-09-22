package com.nathannicolau.fooddeliveryapi.controller;

import com.nathannicolau.fooddeliveryapi.dto.relatorio.DashboardDTO;
import com.nathannicolau.fooddeliveryapi.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping({"/relatorios", "/api/relatorios"})
@Tag(name = "Relatórios e BI", description = "Endpoints analíticos e de governança administrativa")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Obter dados analíticos e consolidados para o Dashboard Executivo")
    public ResponseEntity<DashboardDTO> obterDashboard(
            @RequestParam(name = "periodo", required = false, defaultValue = "SEMANA") String periodo,
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        DashboardDTO dashboard = relatorioService.gerarDashboard(periodo, dataInicio, dataFim);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/exportar-csv")
    @Operation(summary = "Exportar relatório de pedidos e faturamento em CSV")
    public ResponseEntity<byte[]> exportarCsv(
            @RequestParam(name = "periodo", required = false, defaultValue = "SEMANA") String periodo,
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim
    ) {
        byte[] csvBytes = relatorioService.exportarCsv(periodo, dataInicio, dataFim);
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio-vendas-" + periodo.toLowerCase() + ".csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
}
