package com.ducktecnology.pet_register.domain.model;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.enums.TipoSolicitacao;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Solicitacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipo;

    @ManyToOne
    private Animal animal;

    @ManyToOne
    private Usuario usuario;

    private String descricao;
    private String contato;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    private LocalDateTime dataCriacao;
}