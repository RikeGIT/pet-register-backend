package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.model.Veterinario;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.auth.OtpChallengeResponseDTO;
import com.ducktecnology.pet_register.dto.usuario.AtualizarUsuarioPerfilDTO;
import com.ducktecnology.pet_register.dto.usuario.AtualizarUsuarioStatusDTO;
import com.ducktecnology.pet_register.dto.usuario.UsuarioAdminResponseDTO;
import com.ducktecnology.pet_register.dto.usuario.UsuarioRequestDTO;
import com.ducktecnology.pet_register.repository.VeterinarioRepository;
import com.ducktecnology.pet_register.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final VeterinarioRepository veterinarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private void validarAdmin() {
        Usuario usuario = usuarioLogado();

        if (usuario.getPerfil() != Perfil.ADMIN) {
            throw new RuntimeException("Sem permissão para acessar a area administrativa");
        }
    }

    private UsuarioAdminResponseDTO toAdminResponse(Usuario usuario) {
        return new UsuarioAdminResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getTelefone(),
                usuario.getPerfil(),
                Boolean.TRUE.equals(usuario.getAtivo())
        );
    }

    public OtpChallengeResponseDTO criar(UsuarioRequestDTO dto) {
        String senhaCriptografada = passwordEncoder.encode(dto.senha());

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .senha(senhaCriptografada)
                .perfil(dto.perfil())
                .ativo(false)
                .build();

        Usuario salvo = repository.save(usuario);
        if (salvo.getPerfil() == Perfil.VETERINARIO) {
            sincronizarVeterinario(salvo);
        }

        otpService.criarCodigoParaUsuario(salvo, false);

        return new OtpChallengeResponseDTO(
                true,
                salvo.getEmail(),
                "Enviamos um código de confirmação para o seu e-mail."
        );
    }

    public Usuario atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarPorId(id);

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCpf(dto.cpf());
        usuario.setTelefone(dto.telefone());
        usuario.setPerfil(dto.perfil());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        Usuario salvo = repository.save(usuario);
        sincronizarVeterinario(salvo);
        return salvo;
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public List<UsuarioAdminResponseDTO> listarAdmin() {
        validarAdmin();

        return repository.findAll().stream()
                .map(this::toAdminResponse)
                .toList();
    }

    public UsuarioAdminResponseDTO atualizarPerfilAdmin(Long id, AtualizarUsuarioPerfilDTO dto) {
        validarAdmin();

        Usuario usuario = buscarPorId(id);
        Perfil perfilAnterior = usuario.getPerfil();
        usuario.setPerfil(dto.perfil());

        Usuario salvo = repository.save(usuario);
        sincronizarVeterinario(salvo);

        if (perfilAnterior == Perfil.VETERINARIO && dto.perfil() != Perfil.VETERINARIO) {
            veterinarioRepository.findByUsuarioId(salvo.getId())
                    .ifPresent(veterinarioRepository::delete);
        }

        return toAdminResponse(salvo);
    }

    public UsuarioAdminResponseDTO atualizarStatusAdmin(Long id, AtualizarUsuarioStatusDTO dto) {
        validarAdmin();

        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(dto.ativo());

        return toAdminResponse(repository.save(usuario));
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    private void sincronizarVeterinario(Usuario usuario) {
        if (usuario.getPerfil() != Perfil.VETERINARIO) {
            return;
        }

        veterinarioRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Veterinario veterinario = new Veterinario();
                    veterinario.setUsuario(usuario);
                    veterinario.setCrmv("PENDENTE");
                    return veterinarioRepository.save(veterinario);
                });
    }
}