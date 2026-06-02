package com.ducktecnology.pet_register.repository;

import com.ducktecnology.pet_register.domain.enums.StatusAgendamento;
import com.ducktecnology.pet_register.domain.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
        SELECT a FROM Agendamento a
        JOIN FETCH a.animal animal
        JOIN FETCH a.horario horario
        JOIN FETCH horario.servico servico
        LEFT JOIN FETCH horario.veterinario veterinario
        LEFT JOIN FETCH veterinario.usuario usuario
        LEFT JOIN FETCH a.solicitacao solicitacao
        LEFT JOIN FETCH solicitacao.usuario solicitante
        WHERE a.status = :status
          AND horario.dataHoraInicio >= :inicio
          AND horario.dataHoraInicio < :fim
        ORDER BY horario.dataHoraInicio ASC
    """)
    List<Agendamento> listarAgendaDoPeriodo(
            @Param("status") StatusAgendamento status,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

    @Query("""
      SELECT a FROM Agendamento a
      JOIN FETCH a.animal animal
      JOIN FETCH a.horario horario
      JOIN FETCH horario.servico servico
      LEFT JOIN FETCH horario.veterinario veterinario
      LEFT JOIN FETCH veterinario.usuario usuario
      LEFT JOIN FETCH a.solicitacao solicitacao
      LEFT JOIN FETCH solicitacao.usuario solicitante
      WHERE a.status = :status
        AND horario.dataHoraInicio >= :inicio
        AND horario.dataHoraInicio < :fim
      ORDER BY horario.dataHoraInicio ASC
    """)
    List<Agendamento> listarAgendaDoDia(
        @Param("status") StatusAgendamento status,
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    @Query("""
      SELECT a FROM Agendamento a
      JOIN FETCH a.animal animal
      JOIN FETCH a.horario horario
      JOIN FETCH horario.servico servico
      LEFT JOIN FETCH horario.veterinario veterinario
      LEFT JOIN FETCH veterinario.usuario usuario
      LEFT JOIN FETCH a.solicitacao solicitacao
      LEFT JOIN FETCH solicitacao.usuario solicitante
      WHERE a.id = :id
    """)
    Optional<Agendamento> buscarDetalhadoPorId(@Param("id") Long id);
}
