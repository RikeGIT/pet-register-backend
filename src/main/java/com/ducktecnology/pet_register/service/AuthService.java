package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.model.RefreshToken;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.LoginRequestDTO;
import com.ducktecnology.pet_register.dto.auth.AuthResponseDTO;
import com.ducktecnology.pet_register.dto.auth.MeResponseDTO;
import com.ducktecnology.pet_register.dto.auth.RefreshRequestDTO;
import com.ducktecnology.pet_register.exception.UnauthorizedException;
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

    private final UsuarioRepository usuariorepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuariorepository.findByEmail(dto.email())
                .orElseThrow(() -> {
                    System.out.println("USUARIO NÃO ENCONTRADO");
                    return new UnauthorizedException("Email ou senha inválidos");
                });
        boolean senhaValida =
                passwordEncoder.matches(dto.senha(), usuario.getSenha());

        System.out.println("SENHA VALIDA: " + senhaValida);

        if (!senhaValida) {
            throw new UnauthorizedException("Email ou senha inválidos");
        }
        refreshTokenRepository.deleteByUsuario(usuario);
        refreshTokenRepository.flush();

        String accessToken = jwtService.gerarToken(usuario);
        String refreshTokenString = jwtService.gerarRefreshToken(usuario);

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setToken(refreshTokenString);
        refreshToken.setUsuario(usuario);
        refreshToken.setExpiracao(
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDTO(
                accessToken,
                refreshTokenString,
                "Bearer"
        );
    }

    public AuthResponseDTO refresh(RefreshRequestDTO dto) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(dto.refreshToken())
                .orElseThrow(() ->
                        new RuntimeException("Refresh token inválido"));

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