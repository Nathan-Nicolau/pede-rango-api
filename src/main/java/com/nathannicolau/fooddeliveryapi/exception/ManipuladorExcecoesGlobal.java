package com.nathannicolau.fooddeliveryapi.exception;

import com.nathannicolau.fooddeliveryapi.dto.exception.RespostaErroDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ManipuladorExcecoesGlobal {

    // 1. Trata exceções de regras de negócio (ex: E-mail já cadastrado)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<RespostaErroDTO> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        RespostaErroDTO erro = new RespostaErroDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Regra de Negócio Violada",
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 2. Trata erros de validação de DTO (@Valid, @Email, @NotBlank, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaErroDTO> tratarValidacaoDTO(MethodArgumentNotValidException ex) {
        Map<String, String> errosMap = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errosMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        RespostaErroDTO erro = new RespostaErroDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de Validação nos Dados Enviados",
            "Um ou mais campos informados são inválidos.",
            errosMap
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    // 3. Trata exceções genéricas não capturadas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaErroDTO> tratarExcecaoGenerica(Exception ex) {
        RespostaErroDTO erro = new RespostaErroDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno no Servidor",
            ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
