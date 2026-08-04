package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import com.nathannicolau.fooddeliveryapi.domain.pedido.ItemPedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.Pedido;
import com.nathannicolau.fooddeliveryapi.domain.pedido.StatusPedido;
import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import com.nathannicolau.fooddeliveryapi.domain.produto.Produto;
import com.nathannicolau.fooddeliveryapi.dto.pedido.CancelarPedidoDTO;
import com.nathannicolau.fooddeliveryapi.dto.pedido.CriarItemPedidoDTO;
import com.nathannicolau.fooddeliveryapi.dto.pedido.CriarPedidoDTO;
import com.nathannicolau.fooddeliveryapi.dto.pedido.RespostaPedidoDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.ClienteRepository;
import com.nathannicolau.fooddeliveryapi.repository.EntregadorRepository;
import com.nathannicolau.fooddeliveryapi.repository.PedidoRepository;
import com.nathannicolau.fooddeliveryapi.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final EntregadorRepository entregadorRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
        PedidoRepository pedidoRepository,
        ClienteRepository clienteRepository,
        EntregadorRepository entregadorRepository,
        ProdutoRepository produtoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.entregadorRepository = entregadorRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public RespostaPedidoDTO criarPedido(CriarPedidoDTO dto) {
        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new RegraDeNegocioException("O pedido deve conter pelo menos um item.");
        }

        Pedido pedido = new Pedido();

        // 1. Tenta associar Cliente cadastrado por ID ou nome/e-mail
        if (dto.clienteId() != null) {
            Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElse(null);
            pedido.setCliente(cliente);
        } else if (dto.nomeCliente() != null && !dto.nomeCliente().isBlank()) {
            Cliente cliente = clienteRepository.findByUsuarioEmail(dto.nomeCliente())
                .orElseGet(() -> clienteRepository.findAll().stream()
                    .filter(c -> c.getUsuario() != null && c.getUsuario().getNome().equalsIgnoreCase(dto.nomeCliente().trim()))
                    .findFirst().orElse(null));
            pedido.setCliente(cliente);
        }

        // 2. Definir nome do cliente e endereço de entrega
        String nomeCliente = (pedido.getCliente() != null && pedido.getCliente().getUsuario() != null)
            ? pedido.getCliente().getUsuario().getNome()
            : dto.nomeCliente();

        String enderecoEntrega = (pedido.getCliente() != null && pedido.getCliente().getEnderecoPadrao() != null && !pedido.getCliente().getEnderecoPadrao().isBlank())
            ? pedido.getCliente().getEnderecoPadrao()
            : dto.enderecoEntrega();

        if (nomeCliente == null || nomeCliente.isBlank()) {
            throw new RegraDeNegocioException("O nome do cliente é obrigatório para realizar o pedido.");
        }
        if (enderecoEntrega == null || enderecoEntrega.isBlank()) {
            throw new RegraDeNegocioException("O endereço de entrega é obrigatório.");
        }

        pedido.setNomeCliente(nomeCliente);
        pedido.setEnderecoEntrega(enderecoEntrega);

        // 3. Processar itens do pedido
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (CriarItemPedidoDTO itemDTO : dto.itens()) {
            ItemPedido item = new ItemPedido();
            String nomeItem = itemDTO.nome();
            BigDecimal precoItem = itemDTO.precoUnitario();

            if (itemDTO.produtoId() != null) {
                Produto produto = produtoRepository.findById(itemDTO.produtoId())
                    .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + itemDTO.produtoId()));
                if (!Boolean.TRUE.equals(produto.getAtivo())) {
                    throw new RegraDeNegocioException("O produto '" + produto.getNome() + "' está inativo no cardápio.");
                }
                item.setProduto(produto);
                nomeItem = produto.getNome();
                precoItem = produto.getPreco();
            }

            if (nomeItem == null || nomeItem.isBlank()) {
                throw new RegraDeNegocioException("Nome do item do pedido é obrigatório.");
            }
            if (precoItem == null) {
                throw new RegraDeNegocioException("Preço unitário do item é obrigatório.");
            }

            item.setNome(nomeItem);
            item.setQuantidade(itemDTO.quantidade() != null ? itemDTO.quantidade() : 1);
            item.setPrecoUnitario(precoItem);

            pedido.adicionarItem(item);

            BigDecimal subtotal = precoItem.multiply(BigDecimal.valueOf(item.getQuantidade()));
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
    public Page<RespostaPedidoDTO> listarTodosPaginado(
        Boolean somenteValidos,
        StatusPedido status,
        LocalDateTime inicio,
        LocalDateTime fim,
        Pageable pageable
    ) {
        boolean apenasValidos = somenteValidos != null && somenteValidos;
        return pedidoRepository.buscarComFiltrosAvancadosPaginado(apenasValidos, status, inicio, fim, pageable)
            .map(RespostaPedidoDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public Page<RespostaPedidoDTO> listarPorClientePaginado(String email, Pageable pageable) {
        return pedidoRepository.findByClienteUsuarioEmailOrderByCriadoEmDesc(email, pageable)
            .map(RespostaPedidoDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public RespostaPedidoDTO buscarPorCodigoOuId(String codigoOuId) {
        Pedido pedido = encontrarPorCodigoOuId(codigoOuId);
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional
    public RespostaPedidoDTO atualizarStatus(String codigoOuId, StatusPedido novoStatus) {
        Pedido pedido = encontrarPorCodigoOuId(codigoOuId);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("Não é possível alterar o status de um pedido já cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível alterar o status de um pedido já entregue.");
        }

        pedido.setStatus(novoStatus);
        if (novoStatus == StatusPedido.ENTREGUE && pedido.getDataEntrega() == null) {
            pedido.setDataEntrega(LocalDateTime.now());
        }
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    @Transactional
    public RespostaPedidoDTO atribuirEntregador(String codigoOuId, Long entregadorId) {
        Pedido pedido = encontrarPorCodigoOuId(codigoOuId);

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
    public RespostaPedidoDTO cancelarPedido(String codigoOuId, CancelarPedidoDTO dto) {
        Pedido pedido = encontrarPorCodigoOuId(codigoOuId);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("Este pedido já se encontra cancelado.");
        }

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new RegraDeNegocioException("Não é possível cancelar um pedido que já foi entregue.");
        }

        if (pedido.getStatus() == StatusPedido.RECEBIDO) {
            throw new RegraDeNegocioException("Não é possível cancelar um pedido no status RECEBIDO.");
        }

        pedido.cancelar(dto.motivo(), dto.observacoes());
        return RespostaPedidoDTO.daEntidade(pedido);
    }

    private Pedido encontrarPorCodigoOuId(String codigoOuId) {
        return pedidoRepository.findByCodigo(codigoOuId)
            .orElseGet(() -> {
                try {
                    Long id = Long.parseLong(codigoOuId);
                    return pedidoRepository.findById(id)
                        .orElseThrow(() -> new RegraDeNegocioException("Pedido não encontrado com o identificador/código: " + codigoOuId));
                } catch (NumberFormatException e) {
                    throw new RegraDeNegocioException("Pedido não encontrado com o código: " + codigoOuId);
                }
            });
    }
}
