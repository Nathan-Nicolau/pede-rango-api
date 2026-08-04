package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.cliente.CriarClienteDTO;
import com.nathannicolau.fooddeliveryapi.dto.cliente.RespostaClienteDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.ClienteRepository;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import com.nathannicolau.fooddeliveryapi.util.FormatoUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(
        ClienteRepository clienteRepository,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RespostaClienteDTO cadastrar(CriarClienteDTO dto) {
        String emailFormatado = FormatoUtils.formatarEmail(dto.email());
        String cpfFormatado = FormatoUtils.formatarCpf(dto.cpf());
        String telefoneFormatado = FormatoUtils.formatarTelefone(dto.telefone());

        if (usuarioRepository.existsByEmailIgnoreCase(emailFormatado)) {
            throw new RegraDeNegocioException("O e-mail '" + emailFormatado + "' já está cadastrado no sistema.");
        }

        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            String limpo = dto.cpf().replaceAll("\\D", "");
            if (clienteRepository.existsByCpfNormalizado(limpo)) {
                throw new RegraDeNegocioException("O CPF informado já está cadastrado no sistema.");
            }
        }

        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            String limpo = dto.telefone().replaceAll("\\D", "");
            if (clienteRepository.existsByTelefoneNormalizado(limpo)) {
                throw new RegraDeNegocioException("O telefone informado já está cadastrado no sistema.");
            }
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
            dto.nome().trim(),
            emailFormatado,
            senhaCriptografada,
            PerfilUsuario.CLIENTE
        );
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        Cliente cliente = new Cliente(usuarioSalvo, cpfFormatado, telefoneFormatado, dto.enderecoPadrao());
        Cliente salvo = clienteRepository.save(cliente);

        return RespostaClienteDTO.daEntidade(salvo);
    }

    @Transactional(readOnly = true)
    public Page<RespostaClienteDTO> listarTodosPaginado(Pageable pageable) {
        return clienteRepository.findByAtivoTrue(pageable)
            .map(RespostaClienteDTO::daEntidade);
    }

    @Transactional(readOnly = true)
    public RespostaClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado com ID: " + id));
        return RespostaClienteDTO.daEntidade(cliente);
    }

    @Transactional(readOnly = true)
    public RespostaClienteDTO buscarPorEmail(String email) {
        String emailFormatado = FormatoUtils.formatarEmail(email);
        Cliente cliente = clienteRepository.findByUsuarioEmail(emailFormatado)
            .orElseThrow(() -> new RegraDeNegocioException("Perfil de cliente não encontrado para o e-mail: " + email));
        return RespostaClienteDTO.daEntidade(cliente);
    }

    @Transactional
    public RespostaClienteDTO atualizar(Long id, String telefone, String enderecoPadrao) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new RegraDeNegocioException("Cliente não encontrado com ID: " + id));

        if (telefone != null && !telefone.isBlank()) {
            String telefoneFormatado = FormatoUtils.formatarTelefone(telefone);
            cliente.setTelefone(telefoneFormatado);
        }
        if (enderecoPadrao != null && !enderecoPadrao.isBlank()) {
            cliente.setEnderecoPadrao(enderecoPadrao.trim());
        }
        return RespostaClienteDTO.daEntidade(cliente);
    }
}
