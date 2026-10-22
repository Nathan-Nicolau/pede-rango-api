package com.nathannicolau.pederangoapi.domain.pedido;

public enum StatusPedido {
    RECEBIDO, //Pedido foi recebido na plataforma (Pode ser cancelado)
    EM_PREPARO, //Pedido está em preparo (Pode ser cancelado, mas não pode ser atribuído como recebido)
    PRONTO_PARA_ENTREGA, //Status que define que o entregador pode pegar o pedido para entrega (Pode ser cancelado, mas não pode ser retroagido)
    SAIU_PARA_ENTREGA, //Entregador está a caminho (Pode ser cancelado, mas não pode ser retroagido)
    ENTREGUE, //Cliente recebeu o pedido (Não pode ser cancelado, nem mudado o seu status)
    CANCELADO //Foi cancelado por uma das partes do pedido e não pode ser mudado em mais nada
}
