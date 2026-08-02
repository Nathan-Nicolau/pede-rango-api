package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.entregador.CriarEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.dto.entregador.RespostaEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.EntregadorRepository;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EntregadorService {

    private final EntregadorRepository entregadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public EntregadorService(
        EntregadorRepository entregadorRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.entregadorRepository = entregadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RespostaEntregadorDTO cadastrar(CriarEntregadorDTO dto) {
        // 1. Validar se o e-mail informado já está em uso
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("O e-mail '" + dto.email() + "' já está cadastrado no sistema.");
        }

        // 2. Criar e salvar o Usuário com credenciais e perfil ENTREGADOR
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
            dto.nome(),
            dto.email(),
            senhaCriptografada,
            PerfilUsuario.ENTREGADOR
        );
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 3. Criar e salvar o registro operacional de Entregador vinculado ao novo Usuário
        Entregador entregador = new Entregador(usuarioSalvo, dto.telefone(), dto.veiculo(), dto.placa());
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
    public Page<RespostaEntregadorDTO> listarTodosPaginado(Pageable pageable) {
        return entregadorRepository.findByAtivoTrue(pageable)
            .map(RespostaEntregadorDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public List<RespostaEntregadorDTO> listarDisponiveis() {
        return entregadorRepository.findByDisponivelTrueAndAtivoTrue().stream()
            .map(RespostaEntregadorDTO::daEntidade)
            .toList();
    }

    @Transactional(readOnly = true)
    public Page<RespostaEntregadorDTO> listarDisponiveisPaginado(Pageable pageable) {
        return entregadorRepository.findByDisponivelTrueAndAtivoTrue(pageable)
            .map(RespostaEntregadorDTO::daEntidade);
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
