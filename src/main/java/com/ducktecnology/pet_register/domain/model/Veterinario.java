package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "veterinario")
@Data
public class Veterinario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    private String crmv;
}
