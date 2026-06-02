package com.ducktecnology.pet_register.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_confirmacao")
@Data
public class CodigoConfirmacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    private Usuario usuario;

    @Column(name = "codigo_confirmacao", nullable = false)
    private String codigoConfirmacao;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "conta_confirmada", nullable = false)
    private Boolean contaConfirmada = false;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;
}
