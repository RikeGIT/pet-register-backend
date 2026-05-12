package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "atendimento")
@Data
public class Atendimento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "agendamento_id", unique = true)
    private Agendamento agendamento;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    private LocalDateTime dataRegistro;

    @Column(columnDefinition = "TEXT")
    private String diagnosticoPreliminar;
}
