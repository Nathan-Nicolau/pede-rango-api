package com.nathannicolau.fooddeliveryapi.repository;

import com.nathannicolau.fooddeliveryapi.domain.configuracao.ConfiguracaoEstabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoEstabelecimentoRepository extends JpaRepository<ConfiguracaoEstabelecimento, Long> {
}
