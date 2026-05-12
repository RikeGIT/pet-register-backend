package com.ducktecnology.pet_register.dto.animal;

public record AnimalRequestDTO(
        String nome,
        String especie,
        String raca,
        Integer idade,
        Double peso,
        String observacoes
) {
}
