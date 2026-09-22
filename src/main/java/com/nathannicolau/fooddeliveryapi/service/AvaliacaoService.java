package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.avaliacao.AvaliacaoPedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.Pedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import com.nathannicolau.fooddeliveryapi.dto.avaliacao.CriarAvaliacaoDTO;
import com.nathannicolau.fooddeliveryapi.dto.avaliacao.RespostaAvaliacaoDTO;
import com.nathannicolau.fooddeliveryapi.dto.pedido.RespostaPedidoDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.AvaliacaoPedidoRepository;
import com.nathannicolau.fooddeliveryapi.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {

    private final AvaliacaoPedidoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;

    public AvaliacaoService(AvaliacaoPedidoRepository avaliacaoRepository, PedidoRepository pedidoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public RespostaPedidoDTO avaliarPedido(String codigoOuId, CriarAvaliacaoDTO dto) {
        Pedido pedido = encontrarPedidoPorCodigoOuId(codigoOuId);

        if (pedido.getStatus() != StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Apenas pedidos concluídos e entregues podem ser avaliados.");
        }

        if (pedido.getAvaliacao() != null || avaliacaoRepository.findByPedidoId(pedido.getId()).isPresent()) {
            throw new RegraDeNegocioException("Este pedido já foi avaliado anteriormente.");
        }

        AvaliacaoPedido avaliacao = new AvaliacaoPedido();
        avaliacao.setPedido(pedido);
        avaliacao.setCliente(pedido.getCliente());
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario() != null ? dto.comentario().trim() : null);

        AvaliacaoPedido salva = avaliacaoRepository.save(avaliacao);
        pedido.setAvaliacao(salva);

        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional(readOnly = true)
    public RespostaAvaliacaoDTO buscarPorPedido(String codigoOuId) {
        Pedido pedido = encontrarPedidoPorCodigoOuId(codigoOuId);
        AvaliacaoPedido avaliacao = avaliacaoRepository.findByPedidoId(pedido.getId())
            .orElseThrow(() -> new RegraDeNegocioException("Avaliação não encontrada para o pedido informado."));
        return RespostaAvaliacaoDTO.daEntidade(avaliacao);
    }

    private Pedido encontrarPedidoPorCodigoOuId(String codigoOuId) {
        return pedidoRepository.findByCodigo(codigoOuId)
            .orElseGet(() -> {
                try {
                    Long id = Long.parseLong(codigoOuId);
                    return pedidoRepository.findById(id)
                        .orElseThrow(() -> new RegraDeNegocioException("Pedido não encontrado: " + codigoOuId));
                } catch (NumberFormatException e) {
                    throw new RegraDeNegocioException("Pedido não encontrado: " + codigoOuId);
                }
            });
    }
}
