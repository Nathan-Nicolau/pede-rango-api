package com.nathannicolau.fooddeliveryapi.dto.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CriarAvaliacaoDTO(
    @NotNull(message = "A nota da avaliação é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    Integer nota,

    String comentario
) {}
