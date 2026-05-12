package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.animal.AnimalRequestDTO;
import com.ducktecnology.pet_register.dto.animal.AnimalResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
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

        Animal animal = Animal.builder()
                .nome(dto.nome())
                .especie(dto.especie())
                .raca(dto.raca())
                .idade(dto.idade())
                .peso(dto.peso())
                .observacoes(dto.observacoes())
                .tutor(usuario)
                .criadoPor(usuario)
                .criadoEm(Instant.now())
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

        animal.setNome(dto.nome());
        animal.setEspecie(dto.especie());
        animal.setRaca(dto.raca());
        animal.setIdade(dto.idade());
        animal.setPeso(dto.peso());
        animal.setObservacoes(dto.observacoes());

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
                animal.getTutor().getNome()
        );
    }
    public Animal buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Animal não encontrado"));
    }
}
