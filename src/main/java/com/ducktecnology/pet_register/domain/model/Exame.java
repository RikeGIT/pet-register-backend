package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "exame")
@Data
public class Exame {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atendimento_id", nullable = false)
    private Atendimento atendimento;

    private String tipoExame;
    private String urlResultado;
}
