package com.nathannicolau.pederangoapi.domain.pedido;

/**
 * Enum relacionado diretamente a entidade CancelamentoPedido
 * */

public enum MotivoCancelamento {
    DESISTENCIA_CLIENTE,
    ENDERECO_NAO_ENCONTRADO,
    FALTA_DE_ESTOQUE,
    ATRASO_NA_ENTREGA,
    OUTRO
}
