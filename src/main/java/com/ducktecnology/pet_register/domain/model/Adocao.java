package com.ducktecnology.pet_register.domain.model;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Adocao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Animal animal;

    @ManyToOne
    private Usuario interessado; // Usuário logado que quer adotar

    private String mensagem;
    private String telefoneContato;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;
    private LocalDateTime dataSolicitacao;
}
