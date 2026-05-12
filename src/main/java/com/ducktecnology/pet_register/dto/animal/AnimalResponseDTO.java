package com.ducktecnology.pet_register.dto.animal;

public record AnimalResponseDTO(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        Double peso,
        String observacoes,
        Long tutorId,
        String tutorNome
) {
}
