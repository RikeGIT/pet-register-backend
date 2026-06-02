package com.ducktecnology.pet_register.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "especie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToMany(mappedBy = "especie")
    @Builder.Default
    private List<Raca> racas = new ArrayList<>();
}
