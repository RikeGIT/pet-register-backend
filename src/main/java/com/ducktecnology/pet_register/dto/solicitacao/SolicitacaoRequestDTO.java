package com.ducktecnology.pet_register.dto.solicitacao;

import com.ducktecnology.pet_register.domain.enums.TipoSolicitacao;

import java.time.LocalDate;

public record SolicitacaoRequestDTO(
        TipoSolicitacao tipo, // CIRURGIA, CONSULTA, CASTRACAO
        Long animalId,
        Long servicoId,
        LocalDate dataPreferencial,
        String descricao,
        String contato
) {}
