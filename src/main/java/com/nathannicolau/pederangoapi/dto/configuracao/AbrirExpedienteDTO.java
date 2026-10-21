package com.nathannicolau.pederangoapi.dto.configuracao;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AbrirExpedienteDTO {
    private BigDecimal taxaEntregaPadrao;
    private Integer tempoEstimadoMinMinutos;
    private Integer tempoEstimadoMaxMinutos;
}
