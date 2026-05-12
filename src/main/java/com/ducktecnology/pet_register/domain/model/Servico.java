package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "servico")
@Data
public class Servico {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer duracaoMinutos;
}
