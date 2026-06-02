package com.ducktecnology.pet_register.dto.animal;

import java.util.List;

public record EspecieResponseDTO(
        Long id,
        String nome,
        boolean ativo,
        List<RacaResponseDTO> racas
) {
}
