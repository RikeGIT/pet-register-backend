package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findAllByOrderByNomeAsc();

    List<Servico> findByAtivoTrueOrderByNomeAsc();
}
