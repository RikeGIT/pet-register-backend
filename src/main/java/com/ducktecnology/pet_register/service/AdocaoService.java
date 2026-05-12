package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.model.Adocao;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.adocao.AdocaoRequestDTO;
import com.ducktecnology.pet_register.dto.adocao.AdocaoResponseDTO;
import com.ducktecnology.pet_register.repository.AdocaoRepository;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdocaoService {
    private final AdocaoRepository adocaoRepository;
    private final AnimalRepository animalRepository;

    public AdocaoResponseDTO criar(AdocaoRequestDTO dto) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return adocaoRepository.findByInteressado(usuarioLogado).stream()
                .map(a -> new AdocaoResponseDTO(a.getId(), a.getAnimal().getId(), a.getAnimal().getNome(), a.getMensagem(), a.getStatus().name()))
                .toList();
    }
}
