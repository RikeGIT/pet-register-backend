package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Solicitacao;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoRequestDTO;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import com.ducktecnology.pet_register.repository.SolicitacaoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final AnimalRepository animalRepository;

    public SolicitacaoResponseDTO criar(SolicitacaoRequestDTO dto) {

        // Pega o usuário logado via Token JWT
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Valida se o animal existe
        Animal animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        // Monta a Entidade
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setTipo(dto.tipo());
        solicitacao.setAnimal(animal);
        solicitacao.setUsuario(usuarioLogado);
        solicitacao.setDescricao(dto.descricao());
        solicitacao.setContato(dto.contato());
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataCriacao(LocalDateTime.now());

        // Salva no banco
        solicitacaoRepository.save(solicitacao);

        return toResponseDTO(solicitacao);
    }

    public List<SolicitacaoResponseDTO> listarMinhas() {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return solicitacaoRepository.findByUsuario(usuarioLogado).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Conversor auxiliar
    private SolicitacaoResponseDTO toResponseDTO(Solicitacao solicitacao) {
        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getTipo().name(),
                solicitacao.getAnimal().getId(),
                solicitacao.getAnimal().getNome(),
                solicitacao.getDescricao(),
                solicitacao.getContato(),
                solicitacao.getStatus().name(),
                solicitacao.getDataCriacao()
        );
    }
    @Transactional
    public void atualizarStatus(Long id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
        solicitacao.setStatus(novoStatus);
        solicitacaoRepository.save(solicitacao);
    }
}
