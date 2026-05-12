package com.ducktecnology.pet_register.domain.model;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "atendimento_medicamento")
@Data
public class AtendimentoMedicamento {

    @EmbeddedId
    private AtendimentoMedicamentoId id;

    @ManyToOne
    @MapsId("atendimentoId")
    private Atendimento atendimento;

    @ManyToOne
    @MapsId("medicamentoId")
    private Medicamento medicamento;

    private BigDecimal quantidadeUtilizada;
}
