package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Especie;
import com.ducktecnology.pet_register.domain.model.Raca;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.animal.AnimalRequestDTO;
import com.ducktecnology.pet_register.dto.animal.AnimalResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import com.ducktecnology.pet_register.repository.EspecieRepository;
import com.ducktecnology.pet_register.repository.RacaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {
    private final AnimalRepository repository;
    private final EspecieRepository especieRepository;
    private final RacaRepository racaRepository;
    private Usuario usuarioLogado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (Usuario) authentication.getPrincipal();
    }
    private void validarPermissao(Usuario usuario, Animal animal) {

        Perfil perfil = usuario.getPerfil();

        if (perfil == Perfil.ADMIN || perfil == Perfil.VETERINARIO) {
            return;
        }

        boolean donoDoAnimal =
                animal.getTutor().getId().equals(usuario.getId());

        if (perfil == Perfil.TUTOR && donoDoAnimal) {
            return;
        }

        throw new RuntimeException("Sem permissão para acessar este animal");
    }

    @Transactional
    public AnimalResponseDTO criar(AnimalRequestDTO dto) {

        Usuario usuario = usuarioLogado();
        TaxonomiaNormalizada taxonomia = validarTaxonomia(dto.especie(), dto.raca());

        Animal animal = Animal.builder()
                .nome(dto.nome())
            .especie(taxonomia.especie())
            .raca(taxonomia.raca())
                .idade(dto.idade())
                .peso(dto.peso())
                .observacoes(dto.observacoes())
                .tutor(usuario)
                .criadoPor(usuario)
                .criadoEm(Instant.now())
                .fotoUrl(dto.fotoUrl())
                .descricaoPublica(dto.descricaoPublica())
                .publico(dto.publico())
                .destaque(dto.destaque())
                .statusAdocao(dto.statusAdocao())
                .build();

        repository.save(animal);

        return toResponse(animal);
    }

    public List<AnimalResponseDTO> listar() {

        Usuario usuario = usuarioLogado();

        List<Animal> animais;

        if (usuario.getPerfil() == Perfil.TUTOR) {
            animais = repository.findByTutor(usuario);
        } else {
            animais = repository.findAll();
        }

        return animais.stream()
                .map(this::toResponse)
                .toList();
    }

    public AnimalResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioLogado();

        Animal animal = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Animal não encontrado"));

        validarPermissao(usuario, animal);

        return toResponse(animal);
    }

    @Transactional
    public AnimalResponseDTO atualizar(Long id, AnimalRequestDTO dto) {

        Usuario usuario = usuarioLogado();

        Animal animal = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Animal não encontrado"));

        validarPermissao(usuario, animal);
        TaxonomiaNormalizada taxonomia = validarTaxonomia(dto.especie(), dto.raca());

        animal.setNome(dto.nome());
        animal.setEspecie(taxonomia.especie());
        animal.setRaca(taxonomia.raca());
        animal.setIdade(dto.idade());
        animal.setPeso(dto.peso());
        animal.setObservacoes(dto.observacoes());
        animal.setFotoUrl(dto.fotoUrl());
        animal.setDescricaoPublica(dto.descricaoPublica());
        animal.setPublico(dto.publico());
        animal.setDestaque(dto.destaque());
        animal.setStatusAdocao(dto.statusAdocao());

        repository.save(animal);

        return toResponse(animal);
    }

    @Transactional
    public void deletar(Long id) {

        Usuario usuario = usuarioLogado();

        Animal animal = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Animal não encontrado"));

        validarPermissao(usuario, animal);

        repository.delete(animal);
    }

    private AnimalResponseDTO toResponse(Animal animal) {

        return new AnimalResponseDTO(
                animal.getId(),
                animal.getNome(),
                animal.getEspecie(),
                animal.getRaca(),
                animal.getIdade(),
                animal.getPeso(),
                animal.getObservacoes(),
                animal.getTutor().getId(),
                animal.getTutor().getNome(),
                animal.getFotoUrl(),
                animal.getDescricaoPublica(),
                animal.isPublico(),
                animal.isDestaque(),
                animal.getStatusAdocao()
        );
    }
    public Animal buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
    }
    @Transactional
    public void atualizarFoto(Long id, String fotoUrl) {
        Animal animal = buscarEntidadePorId(id);
        animal.setFotoUrl(fotoUrl);
        repository.save(animal);
    }

    private TaxonomiaNormalizada validarTaxonomia(String especieNome, String racaNome) {
        if (especieNome == null || especieNome.isBlank()) {
            throw new RuntimeException("Especie e obrigatoria");
        }

        Especie especie = especieRepository.findByNomeIgnoreCaseAndAtivoTrue(especieNome.trim())
                .orElseThrow(() -> new RuntimeException("Especie invalida ou inativa"));

        if (racaNome == null || racaNome.isBlank()) {
            return new TaxonomiaNormalizada(especie.getNome(), null);
        }

        Raca raca = racaRepository.findByNomeIgnoreCaseAndEspecieIdAndAtivoTrue(racaNome.trim(), especie.getId())
                .orElseThrow(() -> new RuntimeException("Raca invalida para a especie informada"));

        return new TaxonomiaNormalizada(especie.getNome(), raca.getNome());
    }

    private record TaxonomiaNormalizada(String especie, String raca) {}
}
