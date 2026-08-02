package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.cliente.Cliente;
import com.nathannicolau.fooddeliveryapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.cliente.CriarClienteDTO;
import com.nathannicolau.fooddeliveryapi.dto.cliente.RespostaClienteDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.ClienteRepository;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
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
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraDeNegocioException("O e-mail '" + dto.email() + "' já está cadastrado no sistema.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(
            dto.nome(),
            dto.email(),
            senhaCriptografada,
            PerfilUsuario.CLIENTE
        );
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        Cliente cliente = new Cliente(usuarioSalvo, dto.cpf(), dto.telefone(), dto.enderecoPadrao());
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
        Cliente cliente = clienteRepository.findByUsuarioEmail(email)
            .orElseThrow(() -> new RegraDeNegocioException("Perfil de cliente não encontrado para o e-mail: " + email));
        return RespostaClienteDTO.daEntidade(cliente);
    }
}
