package com.nathannicolau.fooddeliveryapi.dto.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record RespostaErroDTO(
    int status,
    String erro,
    String mensagem,
    Map<String, String> errosValidacao,
    LocalDateTime timestamp
) {
    public RespostaErroDTO(int status, String erro, String mensagem) {
        this(status, erro, mensagem, null, LocalDateTime.now());
    }

    public RespostaErroDTO(int status, String erro, String mensagem, Map<String, String> errosValidacao) {
        this(status, erro, mensagem, errosValidacao, LocalDateTime.now());
    }
}
