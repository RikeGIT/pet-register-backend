package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "medicamento")
@Data
public class Medicamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Column(precision = 10, scale = 2)
    private BigDecimal quantidadeEstoque;
    private String unidadeMedida;
}
