package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.model.Servico;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.servico.ServicoRequestDTO;
import com.ducktecnology.pet_register.dto.servico.ServicoResponseDTO;
import com.ducktecnology.pet_register.dto.servico.UpdateServicoRequestDTO;
import com.ducktecnology.pet_register.repository.ServicoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository repository;

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private void validarAdmin() {
        Usuario usuario = usuarioLogado();

        if (usuario.getPerfil() != Perfil.ADMIN) {
            throw new RuntimeException("Sem permissao para acessar a area administrativa");
        }
    }

    private ServicoResponseDTO toResponse(Servico servico) {
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getPreco(),
                servico.getDuracaoMinutos(),
                Boolean.TRUE.equals(servico.getAtivo())
        );
    }

    public List<ServicoResponseDTO> listarPublicos() {
        return repository.findByAtivoTrueOrderByNomeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ServicoResponseDTO> listarAdmin() {
        validarAdmin();

        return repository.findAllByOrderByNomeAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServicoResponseDTO criar(ServicoRequestDTO dto) {
        validarAdmin();

        Servico servico = new Servico();
        servico.setNome(validarTexto(dto.nome(), "Nome obrigatorio"));
        servico.setDescricao(dto.descricao() == null ? "" : dto.descricao().trim());
        servico.setPreco(dto.preco() == null ? 0D : dto.preco());
        servico.setDuracaoMinutos(dto.duracaoMinutos() == null ? 0 : dto.duracaoMinutos());
        servico.setAtivo(dto.ativo() == null || dto.ativo());

        return toResponse(repository.save(servico));
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, UpdateServicoRequestDTO dto) {
        validarAdmin();

        Servico servico = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servico nao encontrado"));

        if (dto.nome() != null && !dto.nome().isBlank()) {
            servico.setNome(dto.nome().trim());
        }

        if (dto.descricao() != null) {
            servico.setDescricao(dto.descricao().trim());
        }

        if (dto.preco() != null) {
            servico.setPreco(dto.preco());
        }

        if (dto.duracaoMinutos() != null) {
            servico.setDuracaoMinutos(dto.duracaoMinutos());
        }

        if (dto.ativo() != null) {
            servico.setAtivo(dto.ativo());
        }

        return toResponse(repository.save(servico));
    }

    @Transactional
    public void deletar(Long id) {
        validarAdmin();
        repository.deleteById(id);
    }

    private String validarTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RuntimeException(mensagem);
        }

        return valor.trim();
    }
}
