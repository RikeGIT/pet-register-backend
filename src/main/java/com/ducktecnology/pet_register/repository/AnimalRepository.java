package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.ducktecnology.pet_register.domain.enums.StatusAdocao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    @Query("SELECT a FROM Animal a WHERE a.publico = true " +
            "AND (:especie IS NULL OR a.especie = :especie) " +
            "AND (:status IS NULL OR a.statusAdocao = :status) " +
            "AND (:search IS NULL OR LOWER(a.nome) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Animal> findVitrine(@Param("especie") String especie,
                             @Param("status") StatusAdocao status,
                             @Param("search") String search,
                             Pageable pageable);

    List<Animal> findByTutor(Usuario tutor);

    // Consultas para a Vitrine Pública
    Page<Animal> findByPublicoTrueAndStatusAdocao(StatusAdocao status, Pageable pageable);

    Page<Animal> findByPublicoTrueAndStatusAdocaoAndEspecie(StatusAdocao status, String especie, Pageable pageable);

    List<Animal> findByPublicoTrueAndDestaqueTrue();
}
