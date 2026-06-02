package com.ducktecnology.pet_register.dto.animal;

public record UpdateRacaDTO(
        String nome,
        Long especieId,
        Boolean ativo
) {
}
