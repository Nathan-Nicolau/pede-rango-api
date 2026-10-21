package com.nathannicolau.pederangoapi.service;

import com.nathannicolau.pederangoapi.domain.cliente.Cliente;
import com.nathannicolau.pederangoapi.domain.usuario.PerfilUsuario;
import com.nathannicolau.pederangoapi.domain.usuario.Usuario;
import com.nathannicolau.pederangoapi.dto.auth.LoginDTO;
import com.nathannicolau.pederangoapi.dto.auth.RegistroDTO;
import com.nathannicolau.pederangoapi.dto.auth.RespostaTokenDTO;
import com.nathannicolau.pederangoapi.exception.RegraDeNegocioException;
import com.nathannicolau.pederangoapi.repository.ClienteRepository;
import com.nathannicolau.pederangoapi.repository.EntregadorRepository;
import com.nathannicolau.pederangoapi.repository.UsuarioRepository;
import com.nathannicolau.pederangoapi.security.JwtTokenProvider;
import com.nathannicolau.pederangoapi.util.FormatoUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EntregadorRepository entregadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AutenticacaoService(
        UsuarioService usuarioService,
        UsuarioRepository usuarioRepository,
        ClienteRepository clienteRepository,
        EntregadorRepository entregadorRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.entregadorRepository = entregadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public RespostaTokenDTO registrar(RegistroDTO dto) {
        String emailFormatado = FormatoUtils.formatarEmail(dto.email());
        String cpfFormatado = FormatoUtils.formatarCpf(dto.cpf());
        String telefoneFormatado = FormatoUtils.formatarTelefone(dto.telefone());

        // Validar unicidade de e-mail (case-insensitive)
        if (usuarioRepository.existsByEmailIgnoreCase(emailFormatado)) {
            throw new RegraDeNegocioException("O e-mail '" + emailFormatado + "' já está cadastrado no sistema.");
        }

        // Validar unicidade de CPF no cadastro
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            if (existeCpf(dto.cpf())) {
                throw new RegraDeNegocioException("O CPF informado já está cadastrado no sistema.");
            }
        }

        // Validar unicidade de Telefone no cadastro
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            if (existeTelefone(dto.telefone())) {
                throw new RegraDeNegocioException("O telefone informado já está cadastrado no sistema.");
            }
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());

        // Cadastro público na tela inicial é EXCLUSIVAMENTE para perfil CLIENTE
        RegistroDTO dtoComPerfilCliente = new RegistroDTO(
            dto.nome().trim(),
            emailFormatado,
            senhaCriptografada,
            PerfilUsuario.CLIENTE
        );

        Usuario usuario = usuarioService.cadastrarUsuario(dtoComPerfilCliente);

        // Garante a criação vinculada do registro na tabela 'clientes' com os dados formatados
        if (!clienteRepository.existsByUsuarioId(usuario.getId())) {
            Cliente cliente = new Cliente(usuario, cpfFormatado, telefoneFormatado, dto.enderecoPadrao());
            clienteRepository.save(cliente);
        }

        String token = jwtTokenProvider.gerarToken(usuario);
        return new RespostaTokenDTO(token, usuario.getEmail(), usuario.getPerfil());
    }

    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return usuarioRepository.existsByEmailIgnoreCase(FormatoUtils.formatarEmail(email));
    }

    @Transactional(readOnly = true)
    public boolean existeCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return false;
        String limpo = cpf.replaceAll("\\D", "");
        if (limpo.isBlank()) return false;
        String formatado = FormatoUtils.formatarCpf(limpo);

        return clienteRepository.existsByCpf(cpf.trim())
            || clienteRepository.existsByCpf(limpo)
            || (formatado != null && clienteRepository.existsByCpf(formatado))
            || clienteRepository.existsByCpfNormalizado(limpo);
    }

    @Transactional(readOnly = true)
    public boolean existeTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) return false;
        String limpo = telefone.replaceAll("\\D", "");
        if (limpo.isBlank()) return false;
        String formatado = FormatoUtils.formatarTelefone(limpo);

        return clienteRepository.existsByTelefone(telefone.trim())
            || clienteRepository.existsByTelefone(limpo)
            || (formatado != null && clienteRepository.existsByTelefone(formatado))
            || clienteRepository.existsByTelefoneNormalizado(limpo)
            || entregadorRepository.existsByTelefoneNormalizado(limpo);
    }

    @Transactional(readOnly = true)
    public RespostaTokenDTO autenticar(LoginDTO dto) {
        String emailFormatado = FormatoUtils.formatarEmail(dto.email());
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailFormatado)
            .orElseThrow(() -> new RegraDeNegocioException("Credenciais inválidas: e-mail ou senha incorretos."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RegraDeNegocioException("A conta de usuário informada está inativa.");
        }

        // Valida senha criptografada via BCrypt
        boolean senhaValida = passwordEncoder.matches(dto.senha(), usuario.getSenha());

        if (!senhaValida) {
            throw new RegraDeNegocioException("Credenciais inválidas: e-mail ou senha incorretos.");
        }

        String token = jwtTokenProvider.gerarToken(usuario);
        return new RespostaTokenDTO(token, usuario.getEmail(), usuario.getPerfil());
    }
}
