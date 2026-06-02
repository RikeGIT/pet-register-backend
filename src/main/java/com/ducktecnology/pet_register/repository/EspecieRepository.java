package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EspecieRepository extends JpaRepository<Especie, Long> {
    boolean existsByNomeIgnoreCase(String nome);

    Optional<Especie> findByNomeIgnoreCase(String nome);

    Optional<Especie> findByNomeIgnoreCaseAndAtivoTrue(String nome);

    List<Especie> findAllByOrderByNomeAsc();

    List<Especie> findByAtivoTrueOrderByNomeAsc();
}
