package com.ducktecnology.pet_register.service;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import com.ducktecnology.pet_register.domain.model.Especie;
import com.ducktecnology.pet_register.domain.model.Raca;
import com.ducktecnology.pet_register.domain.model.Usuario;
import com.ducktecnology.pet_register.dto.animal.CreateEspecieDTO;
import com.ducktecnology.pet_register.dto.animal.CreateRacaDTO;
import com.ducktecnology.pet_register.dto.animal.EspecieResponseDTO;
import com.ducktecnology.pet_register.dto.animal.RacaResponseDTO;
import com.ducktecnology.pet_register.dto.animal.UpdateEspecieDTO;
import com.ducktecnology.pet_register.dto.animal.UpdateRacaDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import com.ducktecnology.pet_register.repository.EspecieRepository;
import com.ducktecnology.pet_register.repository.RacaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaxonomiaService {

    private final EspecieRepository especieRepository;
    private final RacaRepository racaRepository;
    private final AnimalRepository animalRepository;

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

    public List<EspecieResponseDTO> listarPublicas() {
        return especieRepository.findByAtivoTrueOrderByNomeAsc()
                .stream()
                .map(this::toResponseSomenteAtivos)
                .toList();
    }

    public List<EspecieResponseDTO> listarAdmin() {
        validarAdmin();

        return especieRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(this::toResponseAdmin)
                .toList();
    }

    @Transactional
    public EspecieResponseDTO criarEspecie(CreateEspecieDTO dto) {
        validarAdmin();

        String nome = normalizeNome(dto.nome());

        if (especieRepository.existsByNomeIgnoreCase(nome)) {
            throw new RuntimeException("Ja existe uma especie com este nome");
        }

        Especie especie = Especie.builder()
                .nome(nome)
                .ativo(dto.ativo() == null ? true : dto.ativo())
                .build();

        return toResponseAdmin(especieRepository.save(especie));
    }

    @Transactional
    public EspecieResponseDTO atualizarEspecie(Long id, UpdateEspecieDTO dto) {
        validarAdmin();

        Especie especie = buscarEspecie(id);

        if (dto.nome() != null && !dto.nome().isBlank()) {
            String novoNome = normalizeNome(dto.nome());
            especieRepository.findByNomeIgnoreCase(novoNome)
                    .filter(item -> !item.getId().equals(id))
                    .ifPresent(item -> {
                        throw new RuntimeException("Ja existe uma especie com este nome");
                    });
            especie.setNome(novoNome);
        }

        if (dto.ativo() != null) {
            especie.setAtivo(dto.ativo());
        }

        return toResponseAdmin(especieRepository.save(especie));
    }

    @Transactional
    public void deletarEspecie(Long id) {
        validarAdmin();

        Especie especie = buscarEspecie(id);
        boolean utilizada = animalRepository.existsByEspecieIgnoreCase(especie.getNome());

        if (utilizada) {
            throw new RuntimeException("Nao e possivel excluir a especie porque existem animais vinculados");
        }

        especieRepository.delete(especie);
    }

    @Transactional
    public EspecieResponseDTO criarRaca(CreateRacaDTO dto) {
        validarAdmin();

        String nome = normalizeNome(dto.nome());
        Especie especie = buscarEspecie(dto.especieId());

        if (racaRepository.existsByNomeIgnoreCaseAndEspecieId(nome, especie.getId())) {
            throw new RuntimeException("Ja existe uma raca com este nome para a especie informada");
        }

        Raca raca = Raca.builder()
                .nome(nome)
                .especie(especie)
                .ativo(dto.ativo() == null ? true : dto.ativo())
                .build();

        racaRepository.save(raca);

        return toResponseAdmin(buscarEspecie(especie.getId()));
    }

    @Transactional
    public EspecieResponseDTO atualizarRaca(Long id, UpdateRacaDTO dto) {
        validarAdmin();

        Raca raca = buscarRaca(id);
        Especie especieDestino = dto.especieId() != null ? buscarEspecie(dto.especieId()) : raca.getEspecie();

        String novoNome = (dto.nome() == null || dto.nome().isBlank())
                ? raca.getNome()
                : normalizeNome(dto.nome());

        racaRepository.findByNomeIgnoreCaseAndEspecieId(novoNome, especieDestino.getId())
                .filter(item -> !item.getId().equals(raca.getId()))
                .ifPresent(item -> {
                    throw new RuntimeException("Ja existe uma raca com este nome para a especie informada");
                });

        raca.setNome(novoNome);
        raca.setEspecie(especieDestino);

        if (dto.ativo() != null) {
            raca.setAtivo(dto.ativo());
        }

        racaRepository.save(raca);

        return toResponseAdmin(buscarEspecie(especieDestino.getId()));
    }

    @Transactional
    public EspecieResponseDTO deletarRaca(Long id) {
        validarAdmin();

        Raca raca = buscarRaca(id);

        boolean utilizada = animalRepository.existsByEspecieIgnoreCaseAndRacaIgnoreCase(
            raca.getEspecie().getNome(),
            raca.getNome()
        );

        if (utilizada) {
            throw new RuntimeException("Nao e possivel excluir a raca porque existem animais vinculados");
        }

        Long especieId = raca.getEspecie().getId();
        racaRepository.delete(raca);

        return toResponseAdmin(buscarEspecie(especieId));
    }

    private Especie buscarEspecie(Long id) {
        if (id == null) {
            throw new RuntimeException("Especie nao informada");
        }

        return especieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especie nao encontrada"));
    }

    private Raca buscarRaca(Long id) {
        return racaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Raca nao encontrada"));
    }

    private String normalizeNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RuntimeException("Nome obrigatorio");
        }

        return nome.trim();
    }

    private EspecieResponseDTO toResponseAdmin(Especie especie) {
        List<RacaResponseDTO> racas = racaRepository.findByEspecieIdOrderByNomeAsc(especie.getId())
                .stream()
                .map(raca -> new RacaResponseDTO(raca.getId(), raca.getNome(), Boolean.TRUE.equals(raca.getAtivo())))
                .toList();

        return new EspecieResponseDTO(
                especie.getId(),
                especie.getNome(),
                Boolean.TRUE.equals(especie.getAtivo()),
                racas
        );
    }

    private EspecieResponseDTO toResponseSomenteAtivos(Especie especie) {
        List<RacaResponseDTO> racas = racaRepository.findByEspecieIdAndAtivoTrueOrderByNomeAsc(especie.getId())
                .stream()
                .map(raca -> new RacaResponseDTO(raca.getId(), raca.getNome(), true))
                .toList();

        return new EspecieResponseDTO(
                especie.getId(),
                especie.getNome(),
                true,
                racas
        );
    }
}
