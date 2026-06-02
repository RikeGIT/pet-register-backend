package com.ducktecnology.pet_register.domain.model;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(length = 20)
    private String telefone;

    @Column
    @Builder.Default
    private Boolean ativo = true;
}