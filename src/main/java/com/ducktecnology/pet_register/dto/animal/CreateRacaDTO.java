package com.ducktecnology.pet_register.dto.animal;

public record CreateRacaDTO(
        String nome,
        Long especieId,
        Boolean ativo
) {
}
