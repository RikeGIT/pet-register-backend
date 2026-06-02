package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Optional<Veterinario> findByUsuarioId(Long usuarioId);

    Optional<Veterinario> findFirstByOrderByIdAsc();
}
