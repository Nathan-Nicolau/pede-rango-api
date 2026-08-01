package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import com.nathannicolau.fooddeliveryapi.domain.pedido.ItemPedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.Pedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import com.nathannicolau.fooddeliveryapi.dto.pedido.*;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.EntregadorRepository;
import com.nathannicolau.fooddeliveryapi.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EntregadorRepository entregadorRepository;

    public PedidoService(PedidoRepository pedidoRepository, EntregadorRepository entregadorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.entregadorRepository = entregadorRepository;
    }

    @Transactional
    public RespostaPedidoDTO criarPedido(CriarPedidoDTO dto) {
        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new RegraDeNegocioException("O pedido deve conter pelo menos um item.");
        }

        Pedido pedido = new Pedido();
        pedido.setNomeCliente(dto.nomeCliente());
        pedido.setEnderecoEntrega(dto.enderecoEntrega());
        pedido.setStatus(StatusPedido.RECEBIDO);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CriarItemPedidoDTO itemDTO : dto.itens()) {
            ItemPedido item = new ItemPedido();
            item.setNome(itemDTO.nome());
            item.setQuantidade(itemDTO.quantidade());
            item.setPrecoUnitario(itemDTO.precoUnitario());

            pedido.adicionarItem(item);

            BigDecimal subtotal = itemDTO.precoUnitario().multiply(BigDecimal.valueOf(itemDTO.quantidade()));
            valorTotal = valorTotal.add(subtotal);
        }

        pedido.setValorTotal(valorTotal);

        // Gerar código sequencial de exibição comercial (PED-YYYYMMDD-XXXX)
        long proximaSequencia = pedidoRepository.count() + 1;
        pedido.gerarCodigoSequencial(proximaSequencia);

        Pedido salvo = pedidoRepository.save(pedido);
        return RespostaPedidoDTO.daEntidade(salvo);
    }

    @Transactional(readOnly = true)
    public List<RespostaPedidoDTO> listarTodos(Boolean somenteValidos, StatusPedido status, LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos;
        if (somenteValidos != null || status != null || inicio != null || fim != null) {
            boolean apenasValidos = somenteValidos != null && somenteValidos;
            pedidos = pedidoRepository.buscarComFiltrosAvancados(apenasValidos, status, inicio, fim);
        } else {
            pedidos = pedidoRepository.findAll();
        }

        return pedidos.stream()
            .map(RespostaPedidoDTO::daEntidade)
            .toList();
    }

    @Transactional(readOnly = true)
    public RespostaPedidoDTO buscarPorCodigoOuId(String termo) {
        Pedido pedido = encontrarPorCodigoOuId(termo);
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional
    public RespostaPedidoDTO atualizarStatus(String termo, StatusPedido novoStatus) {
        Pedido pedido = encontrarPorCodigoOuId(termo);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("Não é possível alterar o status de um pedido já cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível alterar o status de um pedido já entregue.");
        }

        pedido.setStatus(novoStatus);
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional
    public RespostaPedidoDTO atribuirEntregador(String termo, Long entregadorId) {
        Pedido pedido = encontrarPorCodigoOuId(termo);

        if (pedido.getStatus() == StatusPedido.CANCELADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível atribuir entregador a um pedido finalizado ou cancelado.");
        }

        Entregador entregador = entregadorRepository.findById(entregadorId)
            .orElseThrow(() -> new RegraDeNegocioException("Entregador não encontrado com ID: " + entregadorId));

        if (!Boolean.TRUE.equals(entregador.getAtivo())) {
            throw new RegraDeNegocioException("O entregador informado está inativo no sistema.");
        }

        pedido.setEntregador(entregador);
        if (pedido.getStatus() == StatusPedido.RECEBIDO) {
            pedido.setStatus(StatusPedido.EM_PREPARO);
        }

        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional
    public RespostaPedidoDTO cancelarPedido(String termo, CancelarPedidoDTO dto) {
        Pedido pedido = encontrarPorCodigoOuId(termo);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("Este pedido já se encontra cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível cancelar um pedido que já foi entregue.");
        }

        pedido.cancelar(dto.motivo(), dto.observacoes());
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    private Pedido encontrarPorCodigoOuId(String termo) {
        // Tenta buscar por código
        return pedidoRepository.findByCodigo(termo)
            .orElseGet(() -> {
                // Se o termo for um ID numérico, tenta buscar por ID
                try {
                    Long id = Long.parseLong(termo);
                    return pedidoRepository.findById(id)
                        .orElseThrow(() -> new RegraDeNegocioException("Pedido não encontrado com o identificador: " + termo));
                } catch (NumberFormatException e) {
                    throw new RegraDeNegocioException("Pedido não encontrado com o código: " + termo);
                }
            });
    }
}
