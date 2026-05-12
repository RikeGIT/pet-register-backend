package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;
import com.ducktecnology.pet_register.domain.enums.StatusHorario;

import java.time.LocalDateTime;

@Entity
@Table(name = "horario_atendimento")
@Data
public class HorarioAtendimento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;

    @Enumerated(EnumType.STRING)
    private StatusHorario status;
}
