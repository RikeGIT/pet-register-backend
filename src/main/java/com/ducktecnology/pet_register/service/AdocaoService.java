package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.model.Adocao;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.adocao.AdocaoRequestDTO;
import com.ducktecnology.pet_register.dto.adocao.AdocaoResponseDTO;
import com.ducktecnology.pet_register.repository.AdocaoRepository;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdocaoService {
    private final AdocaoRepository adocaoRepository;
    private final AnimalRepository animalRepository;

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private void validarAcessoModeracao(Usuario usuario) {
        if (usuario.getPerfil() == Perfil.ADMIN || usuario.getPerfil() == Perfil.VETERINARIO) {
            return;
        }

        throw new RuntimeException("Sem permissão para moderar adoções");
    }

    public AdocaoResponseDTO criar(AdocaoRequestDTO dto) {
        Usuario usuarioLogado = usuarioLogado();
        Animal animal = animalRepository.findById(dto.animalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));

        Adocao adocao = new Adocao();
        adocao.setAnimal(animal);
        adocao.setInteressado(usuarioLogado);
        adocao.setMensagem(dto.mensagem());
        adocao.setTelefoneContato(dto.telefoneContato());
        adocao.setStatus(StatusSolicitacao.PENDENTE);
        adocao.setDataSolicitacao(LocalDateTime.now());

        adocaoRepository.save(adocao);
        return new AdocaoResponseDTO(adocao.getId(), animal.getId(), animal.getNome(), adocao.getMensagem(), adocao.getStatus().name());
    }

    public List<AdocaoResponseDTO> listarMinhas() {
        Usuario usuarioLogado = usuarioLogado();
        return adocaoRepository.findByInteressado(usuarioLogado).stream()
                .map(a -> new AdocaoResponseDTO(a.getId(), a.getAnimal().getId(), a.getAnimal().getNome(), a.getMensagem(), a.getStatus().name()))
                .toList();
    }

    public List<AdocaoResponseDTO> listarTodas() {
        Usuario usuarioLogado = usuarioLogado();
        validarAcessoModeracao(usuarioLogado);

        return adocaoRepository.findAll().stream()
                .map(a -> new AdocaoResponseDTO(a.getId(), a.getAnimal().getId(), a.getAnimal().getNome(), a.getMensagem(), a.getStatus().name()))
                .toList();
    }

    @Transactional
    public void atualizarStatus(Long id, StatusSolicitacao novoStatus) {
        Usuario usuarioLogado = usuarioLogado();
        validarAcessoModeracao(usuarioLogado);

        Adocao adocao = adocaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adoção não encontrada"));

        adocao.setStatus(novoStatus);

        // Se a adoção for aprovada, atualiza o status do animal
        if (novoStatus == StatusSolicitacao.APROVADO) {
            Animal animal = adocao.getAnimal();
            animal.setStatusAdocao(com.ducktecnology.pet_register.domain.enums.StatusAdocao.EM_PROCESSO);
            animalRepository.save(animal);
        }

        adocaoRepository.save(adocao);
    }
}
