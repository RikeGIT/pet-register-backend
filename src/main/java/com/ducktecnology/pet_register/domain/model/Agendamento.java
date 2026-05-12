package com.ducktecnology.pet_register.domain.model;
import com.ducktecnology.pet_register.domain.enums.StatusAgendamento;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "agendamento")
@Data
public class Agendamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @OneToOne
    @JoinColumn(name = "horario_id", unique = true)
    private HorarioAtendimento horario;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;
}
