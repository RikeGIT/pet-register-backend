package com.ducktecnology.pet_register.dto.solicitacao;

import com.ducktecnology.pet_register.domain.enums.TipoSolicitacao;

public record SolicitacaoRequestDTO(
        TipoSolicitacao tipo, // CIRURGIA, CONSULTA, CASTRACAO
        Long animalId,
        String descricao,
        String contato
) {}
