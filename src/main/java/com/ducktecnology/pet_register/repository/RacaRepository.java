package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Raca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RacaRepository extends JpaRepository<Raca, Long> {
    boolean existsByNomeIgnoreCaseAndEspecieId(String nome, Long especieId);

    Optional<Raca> findByNomeIgnoreCaseAndEspecieId(String nome, Long especieId);

    Optional<Raca> findByNomeIgnoreCaseAndEspecieIdAndAtivoTrue(String nome, Long especieId);

    List<Raca> findByEspecieIdOrderByNomeAsc(Long especieId);

    List<Raca> findByEspecieIdAndAtivoTrueOrderByNomeAsc(Long especieId);
}
