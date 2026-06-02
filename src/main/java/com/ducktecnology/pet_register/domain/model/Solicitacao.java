package com.ducktecnology.pet_register.domain.model;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.domain.enums.TipoSolicitacao;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

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
    private Servico servico;

    @ManyToOne
    private Usuario usuario;

    private LocalDate dataPreferencial;

    private String descricao;
    private String contato;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    @OneToOne(mappedBy = "solicitacao", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Agendamento agendamento;

    private LocalDateTime dataCriacao;
}