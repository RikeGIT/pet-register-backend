package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ducktecnology.pet_register.domain.enums.StatusAdocao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByTutor(Usuario tutor);

    // Consultas para a Vitrine Pública
    Page<Animal> findByPublicoTrueAndStatusAdocao(StatusAdocao status, Pageable pageable);

    Page<Animal> findByPublicoTrueAndStatusAdocaoAndEspecie(StatusAdocao status, String especie, Pageable pageable);

    List<Animal> findByPublicoTrueAndDestaqueTrue();
}
