package com.ducktecnology.pet_register.domain.model;
import com.ducktecnology.pet_register.domain.enums.StatusAgendamento;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Data;
import lombok.ToString;

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
    @JoinColumn(name = "solicitacao_id", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Solicitacao solicitacao;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "horario_id", unique = true)
    private HorarioAtendimento horario;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;
}
