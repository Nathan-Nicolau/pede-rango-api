package com.nathannicolau.pederangoapi.dto.configuracao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AtualizarConfiguracaoDTO {
    @NotBlank(message = "O nome da loja é obrigatório")
    private String nomeLoja;

    private String telefoneContato;

    private String chavePix;

    @NotNull(message = "A taxa de entrega padrão é obrigatória")
    @DecimalMin(value = "0.00", message = "A taxa de entrega não pode ser negativa")
    private BigDecimal taxaEntregaPadrao;

    @NotNull(message = "O tempo estimado mínimo é obrigatório")
    @Min(value = 5, message = "O tempo mínimo deve ser de pelo menos 5 minutos")
    private Integer tempoEstimadoMinMinutos;

    @NotNull(message = "O tempo estimado máximo é obrigatório")
    @Min(value = 5, message = "O tempo máximo deve ser de pelo menos 5 minutos")
    private Integer tempoEstimadoMaxMinutos;
}
