package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.model.RefreshToken;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.LoginRequestDTO;
import com.ducktecnology.pet_register.dto.auth.AuthResponseDTO;
import com.ducktecnology.pet_register.dto.auth.MeResponseDTO;
import com.ducktecnology.pet_register.dto.auth.OtpChallengeResponseDTO;
import com.ducktecnology.pet_register.dto.auth.OtpVerificationRequestDTO;
import com.ducktecnology.pet_register.dto.auth.RefreshRequestDTO;
import com.ducktecnology.pet_register.exception.UnauthorizedException;
import com.ducktecnology.pet_register.repository.CodigoConfirmacaoRepository;
import com.ducktecnology.pet_register.repository.RefreshTokenRepository;
import com.ducktecnology.pet_register.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final CodigoConfirmacaoRepository codigoConfirmacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OtpService otpService;

    @Transactional
    public OtpChallengeResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Email ou senha inválidos"));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            boolean contaPendente = codigoConfirmacaoRepository.findByUsuario(usuario).isPresent();

            if (!contaPendente) {
                throw new UnauthorizedException("Sua conta está bloqueada. Fale com o suporte.");
            }

            otpService.criarCodigoParaUsuario(usuario, false);

            return new OtpChallengeResponseDTO(
                    true,
                    usuario.getEmail(),
                    "Reenviamos o código de confirmação para o seu e-mail."
            );
        }

        boolean senhaValida = passwordEncoder.matches(dto.senha(), usuario.getSenha());
        if (!senhaValida) {
            throw new UnauthorizedException("Email ou senha inválidos");
        }

        otpService.criarCodigoParaUsuario(usuario, true);

        return new OtpChallengeResponseDTO(
                true,
                usuario.getEmail(),
                "Enviamos um código de verificação para o seu e-mail."
        );
    }

    @Transactional
    public AuthResponseDTO verificarOtp(OtpVerificationRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UnauthorizedException("Código ou e-mail inválidos"));

        otpService.validarCodigo(usuario, dto.codigo());

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            usuario.setAtivo(true);
            usuarioRepository.save(usuario);
        }

        refreshTokenRepository.deleteByUsuario(usuario);
        refreshTokenRepository.flush();

        String accessToken = jwtService.gerarToken(usuario);
        String refreshTokenString = jwtService.gerarRefreshToken(usuario);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenString);
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiracao(Instant.now().plus(7, ChronoUnit.DAYS));

        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDTO(accessToken, refreshTokenString, "Bearer");
    }

    public AuthResponseDTO refresh(RefreshRequestDTO dto) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(dto.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (refreshToken.getExpiracao().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expirado");
        }

        Usuario usuario = refreshToken.getUsuario();

        String newAccessToken = jwtService.gerarToken(usuario);

        return new AuthResponseDTO(
                newAccessToken,
                refreshToken.getToken(),
                "Bearer"
        );
    }

    public MeResponseDTO me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        return new MeResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getPerfil()
        );
    }
}