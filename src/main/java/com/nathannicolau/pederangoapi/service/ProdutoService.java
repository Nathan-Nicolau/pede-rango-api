package com.nathannicolau.pederangoapi.service;

import com.nathannicolau.pederangoapi.domain.produto.Produto;
import com.nathannicolau.pederangoapi.dto.produto.CriarProdutoDTO;
import com.nathannicolau.pederangoapi.dto.produto.RespostaProdutoDTO;
import com.nathannicolau.pederangoapi.exception.RegraDeNegocioException;
import com.nathannicolau.pederangoapi.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public RespostaProdutoDTO cadastrar(CriarProdutoDTO dto) {
        Produto produto = new Produto(dto.nome(), dto.descricao(), dto.preco(), dto.categoria());
        Produto salvo = produtoRepository.save(produto);
        return RespostaProdutoDTO.daEntidade(salvo);
    }

    @Transactional
    public RespostaProdutoDTO atualizar(Long id, CriarProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + id));
        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setCategoria(dto.categoria());
        return RespostaProdutoDTO.daEntidade(produto);
    }

    @Transactional(readOnly = true)
    public Page<RespostaProdutoDTO> listarTodosPaginado(String categoria, Pageable pageable) {
        Page<Produto> produtos;
        if (categoria != null && !categoria.isBlank() && !"ALL".equalsIgnoreCase(categoria)) {
            produtos = produtoRepository.findByCategoriaAndAtivoTrue(categoria, pageable);
        } else {
            produtos = produtoRepository.findByAtivoTrue(pageable);
        }
        return produtos.map(RespostaProdutoDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public RespostaProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + id));
        return RespostaProdutoDTO.daEntidade(produto);
    }

    @Transactional
    public RespostaProdutoDTO atualizarStatus(Long id, boolean ativo) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado com ID: " + id));
        produto.setAtivo(ativo);
        return RespostaProdutoDTO.daEntidade(produto);
    }
}
