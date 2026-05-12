package com.ducktecnology.pet_register.dto.animal;
import com.ducktecnology.pet_register.domain.enums.StatusAdocao;

public record AnimalRequestDTO(
        String nome,
        String especie,
        String raca,
        Integer idade,
        Double peso,
        String observacoes,
        String fotoUrl,
        String descricaoPublica,
        boolean publico,
        StatusAdocao statusAdocao,
        boolean destaque
) {}