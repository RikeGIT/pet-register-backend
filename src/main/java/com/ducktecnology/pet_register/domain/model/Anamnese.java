package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "anamnese")
@Data
public class Anamnese {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "atendimento_id", unique = true)
    private Atendimento atendimento;

    @Column(columnDefinition = "TEXT")
    private String queixaPrincipal;
}