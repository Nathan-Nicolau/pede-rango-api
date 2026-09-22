package com.nathannicolau.fooddeliveryapi.dto.configuracao;

import com.nathannicolau.fooddeliveryapi.domain.configuracao.ConfiguracaoEstabelecimento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracaoEstabelecimentoDTO {
    private Long id;
    private String nomeLoja;
    private String telefoneContato;
    private String chavePix;
    private BigDecimal taxaEntregaPadrao;
    private Integer tempoEstimadoMinMinutos;
    private Integer tempoEstimadoMaxMinutos;
    private Boolean lojaAberta;
    private Boolean operacionalmenteAbertaHoje;
    private Boolean expedienteIniciadoHoje;
    private LocalDate dataExpediente;
    private LocalDateTime abertoEm;
    private LocalDateTime fechadoEm;
    private LocalDateTime atualizadoEm;

    public static ConfiguracaoEstabelecimentoDTO deEntidade(ConfiguracaoEstabelecimento entity) {
        return ConfiguracaoEstabelecimentoDTO.builder()
            .id(entity.getId())
            .nomeLoja(entity.getNomeLoja())
            .telefoneContato(entity.getTelefoneContato())
            .chavePix(entity.getChavePix())
            .taxaEntregaPadrao(entity.getTaxaEntregaPadrao())
            .tempoEstimadoMinMinutos(entity.getTempoEstimadoMinMinutos())
            .tempoEstimadoMaxMinutos(entity.getTempoEstimadoMaxMinutos())
            .lojaAberta(entity.getLojaAberta())
            .operacionalmenteAbertaHoje(entity.isOperacionalmenteAbertaHoje())
            .expedienteIniciadoHoje(entity.isExpedienteIniciadoHoje())
            .dataExpediente(entity.getDataExpediente())
            .abertoEm(entity.getAbertoEm())
            .fechadoEm(entity.getFechadoEm())
            .atualizadoEm(entity.getAtualizadoEm())
            .build();
    }
}
