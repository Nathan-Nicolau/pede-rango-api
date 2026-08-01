package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.entregador.CriarEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.dto.entregador.RespostaEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.EntregadorRepository;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregadorService {

    private final EntregadorRepository entregadorRepository;
    private final UsuarioRepository usuarioRepository;

    public EntregadorService(EntregadorRepository entregadorRepository, UsuarioRepository usuarioRepository) {
        this.entregadorRepository = entregadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public RespostaEntregadorDTO cadastrar(CriarEntregadorDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
            .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado com ID: " + dto.usuarioId()));

        if (entregadorRepository.findByUsuarioId(dto.usuarioId()).isPresent()) {
            throw new RegraDeNegocioException("O usuário informado já possui um perfil de entregador cadastrado.");
        }

        usuario.setPerfil(PerfilUsuario.ENTREGADOR);

        Entregador entregador = new Entregador(usuario, dto.telefone(), dto.veiculo(), dto.placa());
        Entregador salvo = entregadorRepository.save(entregador);

        return RespostaEntregadorDTO.daEntidade(salvo);
    }

    @Transactional(readOnly = true)
    public List<RespostaEntregadorDTO> listarTodos() {
        return entregadorRepository.findByAtivoTrue().stream()
            .map(RespostaEntregadorDTO::daEntidade)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RespostaEntregadorDTO> listarDisponiveis() {
        return entregadorRepository.findByDisponivelTrueAndAtivoTrue().stream()
            .map(RespostaEntregadorDTO::daEntidade)
            .toList();
    }

    @Transactional(readOnly = true)
    public RespostaEntregadorDTO buscarPorId(Long id) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Entregador não encontrado com ID: " + id));
        return RespostaEntregadorDTO.daEntidade(entregador);
    }

    @Transactional
    public RespostaEntregadorDTO alternarDisponibilidade(Long id, boolean disponivel) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Entregador não encontrado com ID: " + id));
        entregador.setDisponivel(disponivel);
        return RespostaEntregadorDTO.daEntidade(entregador);
    }
}
