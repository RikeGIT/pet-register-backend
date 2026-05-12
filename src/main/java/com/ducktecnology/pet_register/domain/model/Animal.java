package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import com.ducktecnology.pet_register.domain.enums.StatusAdocao; // Criar este enum

@Entity
@Table(name = "animals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Animal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private Integer idade;
    private Double peso;

    @Column(length = 2000)
    private String observacoes;

    // --- NOVOS CAMPOS ---
    private String fotoUrl;

    @Column(columnDefinition = "TEXT")
    private String descricaoPublica;

    private boolean publico = false; // Se aparece na home

    @Enumerated(EnumType.STRING)
    private StatusAdocao statusAdocao = StatusAdocao.DISPONIVEL;

    private boolean destaque = false;
    // --------------------

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = true)
    private Usuario tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuario criadoPor;

    private Instant criadoEm;
}