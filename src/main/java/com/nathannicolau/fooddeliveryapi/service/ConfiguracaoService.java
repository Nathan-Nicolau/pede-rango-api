package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.configuracao.ConfiguracaoEstabelecimento;
import com.nathannicolau.fooddeliveryapi.dto.configuracao.AbrirExpedienteDTO;
import com.nathannicolau.fooddeliveryapi.dto.configuracao.AtualizarConfiguracaoDTO;
import com.nathannicolau.fooddeliveryapi.dto.configuracao.ConfiguracaoEstabelecimentoDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.ConfiguracaoEstabelecimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfiguracaoService {

    private final ConfiguracaoEstabelecimentoRepository repository;

    @Transactional
    public ConfiguracaoEstabelecimento obterEntidade() {
        ConfiguracaoEstabelecimento config = repository.findById(1L)
            .orElseGet(() -> {
                ConfiguracaoEstabelecimento nova = new ConfiguracaoEstabelecimento();
                nova.setId(1L);
                nova.setNomeLoja("Pede Rango Delivery");
                nova.setTaxaEntregaPadrao(new BigDecimal("5.00"));
                nova.setTempoEstimadoMinMinutos(30);
                nova.setTempoEstimadoMaxMinutos(45);
                nova.setLojaAberta(true);
                nova.setDataExpediente(LocalDate.now());
                nova.setAbertoEm(LocalDateTime.now());
                return repository.save(nova);
            });

        LocalDate hoje = LocalDate.now();
        if (config.getDataExpediente() == null || config.getDataExpediente().isBefore(hoje)) {
            if (Boolean.TRUE.equals(config.getLojaAberta())) {
                config.setLojaAberta(false);
                config.setFechadoEm(LocalDateTime.now());
                repository.save(config);
            }
        }

        return config;
    }

    @Transactional(readOnly = true)
    public ConfiguracaoEstabelecimentoDTO obterDTO() {
        ConfiguracaoEstabelecimento config = obterEntidade();
        return ConfiguracaoEstabelecimentoDTO.deEntidade(config);
    }

    @Transactional(readOnly = true)
    public boolean isOperacionalmenteAbertaHoje() {
        ConfiguracaoEstabelecimento config = obterEntidade();
        return config.isOperacionalmenteAbertaHoje();
    }

    @Transactional(readOnly = true)
    public BigDecimal obterTaxaEntregaPadrao() {
        ConfiguracaoEstabelecimento config = obterEntidade();
        return config.getTaxaEntregaPadrao() != null ? config.getTaxaEntregaPadrao() : BigDecimal.ZERO;
    }

    @Transactional
    public ConfiguracaoEstabelecimentoDTO abrirExpediente(AbrirExpedienteDTO dto) {
        ConfiguracaoEstabelecimento config = obterEntidade();
        config.setDataExpediente(LocalDate.now());
        config.setLojaAberta(true);
        config.setAbertoEm(LocalDateTime.now());
        config.setFechadoEm(null);

        if (dto != null) {
            if (dto.getTaxaEntregaPadrao() != null && dto.getTaxaEntregaPadrao().compareTo(BigDecimal.ZERO) >= 0) {
                config.setTaxaEntregaPadrao(dto.getTaxaEntregaPadrao());
            }
            if (dto.getTempoEstimadoMinMinutos() != null && dto.getTempoEstimadoMinMinutos() >= 5) {
                config.setTempoEstimadoMinMinutos(dto.getTempoEstimadoMinMinutos());
            }
            if (dto.getTempoEstimadoMaxMinutos() != null && dto.getTempoEstimadoMaxMinutos() >= 5) {
                config.setTempoEstimadoMaxMinutos(dto.getTempoEstimadoMaxMinutos());
            }
        }

        repository.save(config);
        return ConfiguracaoEstabelecimentoDTO.deEntidade(config);
    }

    @Transactional
    public ConfiguracaoEstabelecimentoDTO fecharExpediente() {
        ConfiguracaoEstabelecimento config = obterEntidade();
        config.setLojaAberta(false);
        config.setDataExpediente(null);
        config.setFechadoEm(LocalDateTime.now());
        repository.save(config);
        return ConfiguracaoEstabelecimentoDTO.deEntidade(config);
    }

    @Transactional
    public ConfiguracaoEstabelecimentoDTO alternarStatus() {
        ConfiguracaoEstabelecimento config = obterEntidade();
        boolean novoStatus = !Boolean.TRUE.equals(config.getLojaAberta());
        config.setLojaAberta(novoStatus);

        if (novoStatus) {
            config.setDataExpediente(LocalDate.now());
            config.setAbertoEm(LocalDateTime.now());
            config.setFechadoEm(null);
        } else {
            config.setFechadoEm(LocalDateTime.now());
        }

        repository.save(config);
        return ConfiguracaoEstabelecimentoDTO.deEntidade(config);
    }

    @Transactional
    public ConfiguracaoEstabelecimentoDTO atualizarConfiguracoes(AtualizarConfiguracaoDTO dto) {
        if (dto.getTempoEstimadoMinMinutos() > dto.getTempoEstimadoMaxMinutos()) {
            throw new RegraDeNegocioException("O tempo mínimo estimado não pode ser maior que o tempo máximo.");
        }

        ConfiguracaoEstabelecimento config = obterEntidade();
        config.setNomeLoja(dto.getNomeLoja().trim());
        config.setTelefoneContato(dto.getTelefoneContato() != null ? dto.getTelefoneContato().trim() : null);
        config.setChavePix(dto.getChavePix() != null ? dto.getChavePix().trim() : null);
        config.setTaxaEntregaPadrao(dto.getTaxaEntregaPadrao());
        config.setTempoEstimadoMinMinutos(dto.getTempoEstimadoMinMinutos());
        config.setTempoEstimadoMaxMinutos(dto.getTempoEstimadoMaxMinutos());

        repository.save(config);
        return ConfiguracaoEstabelecimentoDTO.deEntidade(config);
    }
}
