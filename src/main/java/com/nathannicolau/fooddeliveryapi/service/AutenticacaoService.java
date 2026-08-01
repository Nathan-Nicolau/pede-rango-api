package com.nathannicolau.fooddeliveryapi.service;

import com.nathannicolau.fooddeliveryapi.domain.usuario.Usuario;
import com.nathannicolau.fooddeliveryapi.dto.auth.LoginDTO;
import com.nathannicolau.fooddeliveryapi.dto.auth.RegistroDTO;
import com.nathannicolau.fooddeliveryapi.dto.auth.RespostaTokenDTO;
import com.nathannicolau.fooddeliveryapi.exception.RegraDeNegocioException;
import com.nathannicolau.fooddeliveryapi.repository.UsuarioRepository;
import com.nathannicolau.fooddeliveryapi.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AutenticacaoService(
        UsuarioService usuarioService,
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtTokenProvider jwtTokenProvider
    ) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public RespostaTokenDTO registrar(RegistroDTO dto) {
        String senhaCriptografada = passwordEncoder.encode(dto.senha());
        RegistroDTO dtoComSenhaCriptografada = new RegistroDTO(dto.nome(), dto.email(), senhaCriptografada, dto.perfil());

        Usuario usuario = usuarioService.cadastrarUsuario(dtoComSenhaCriptografada);
        String token = jwtTokenProvider.gerarToken(usuario);

        return new RespostaTokenDTO(token, usuario.getEmail(), usuario.getPerfil());
    }

    @Transactional(readOnly = true)
    public RespostaTokenDTO autenticar(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
            .orElseThrow(() -> new RegraDeNegocioException("Credenciais inválidas: e-mail ou senha incorretos."));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new RegraDeNegocioException("A conta de usuário informada está inativa.");
        }

        // Valida senha criptografada via BCrypt ou fallback para texto limpo se for dado legado do seed
        boolean senhaValida = passwordEncoder.matches(dto.senha(), usuario.getSenha()) || usuario.getSenha().equals(dto.senha());

        if (!senhaValida) {
            throw new RegraDeNegocioException("Credenciais inválidas: e-mail ou senha incorretos.");
        }

        String token = jwtTokenProvider.gerarToken(usuario);
        return new RespostaTokenDTO(token, usuario.getEmail(), usuario.getPerfil());
    }
}
