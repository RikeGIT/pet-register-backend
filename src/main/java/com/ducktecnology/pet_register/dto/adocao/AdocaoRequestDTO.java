package com.ducktecnology.pet_register.dto.adocao;

public record AdocaoRequestDTO(
        Long animalId,
        String mensagem,
        String telefoneContato
) {
}
