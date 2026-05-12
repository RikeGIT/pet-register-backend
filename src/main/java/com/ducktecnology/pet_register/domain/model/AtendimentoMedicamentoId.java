package com.ducktecnology.pet_register.domain.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class AtendimentoMedicamentoId implements Serializable {
    private Long atendimentoId;
    private Long medicamentoId;
}
