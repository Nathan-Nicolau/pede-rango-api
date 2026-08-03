package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.entregador.Entregador;
import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.entregador.CriarEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.dto.entregador.RespostaEntregadorDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.EntregadorRepository;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import com.nathannicolau.fooddeliveryapi.util.FormatoUtils;
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
        String emailFormatado = FormatoUtils.formatarEmail(dto.email());
        String telefoneFormatado = FormatoUtils.formatarTelefone(dto.telefone());

        // 1. Validar se o e-mail informado já está em uso
        if (usuarioRepository.existsByEmailIgnoreCase(emailFormatado)) {
            throw new RegraDeNegocioException("O e-mail '" + emailFormatado + "' já está cadastrado no sistema.");
        }

        // 2. Criar e salvar o Usuário com credenciais e perfil ENTREGADOR
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
            dto.nome().trim(),
            emailFormatado,
            senhaCriptografada,
            PerfilUsuario.ENTREGADOR
        );
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 3. Criar e salvar o registro operacional de Entregador vinculado ao novo Usuário
        Entregador entregador = new Entregador(usuarioSalvo, telefoneFormatado, dto.veiculo(), dto.placa());
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

    @Transactional(readOnly = true)
    public RespostaEntregadorDTO buscarPorEmail(String email) {
        String emailFormatado = FormatoUtils.formatarEmail(email);
        Entregador entregador = entregadorRepository.findByUsuarioEmail(emailFormatado)
            .orElseThrow(() -> new RegraDeNegocioException("Entregador não encontrado com e-mail: " + email));
        return RespostaEntregadorDTO.daEntidade(entregador);
    }

    @Transactional
    public RespostaEntregadorDTO atualizarTelefone(Long id, String telefone) {
        Entregador entregador = entregadorRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Entregador não encontrado com ID: " + id));
        if (telefone != null && !telefone.isBlank()) {
            String telefoneFormatado = FormatoUtils.formatarTelefone(telefone);
            entregador.setTelefone(telefoneFormatado);
        }
        return RespostaEntregadorDTO.daEntidade(entregador);
    }
}
