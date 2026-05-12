package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.StatusAdocao;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.dto.animal.PublicAnimalResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicAnimalService {

    private final AnimalRepository repository;

    public Page<PublicAnimalResponseDTO> listarPublicos(String especie, Pageable pageable) {
        Page<Animal> animais;

        if (especie != null && !especie.isBlank()) {
            animais = repository.findByPublicoTrueAndStatusAdocaoAndEspecie(StatusAdocao.DISPONIVEL, especie, pageable);
        } else {
            animais = repository.findByPublicoTrueAndStatusAdocao(StatusAdocao.DISPONIVEL, pageable);
        }

        return animais.map(this::toPublicResponse);
    }

    public List<PublicAnimalResponseDTO> listarDestaques() {
        return repository.findByPublicoTrueAndDestaqueTrue()
                .stream()
                .map(this::toPublicResponse)
                .collect(Collectors.toList());
    }

    private PublicAnimalResponseDTO toPublicResponse(Animal animal) {
        return new PublicAnimalResponseDTO(
                animal.getId(), animal.getNome(), animal.getEspecie(),
                animal.getRaca(), animal.getIdade(), animal.getPeso(),
                animal.getFotoUrl(), animal.getDescricaoPublica(),
                animal.getStatusAdocao(), animal.isDestaque()
        );
    }
}