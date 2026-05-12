package com.ducktecnology.pet_register.dto.animal;

import com.ducktecnology.pet_register.domain.enums.StatusAdocao;

public record PublicAnimalResponseDTO(
        Long id,
        String nome,
        String especie,
        String raca,
        Integer idade,
        Double peso,
        String fotoUrl,
        String descricaoPublica,
        StatusAdocao statusAdocao,
        boolean destaque
) {}