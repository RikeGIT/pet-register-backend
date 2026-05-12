package com.ducktecnology.pet_register.dto.adocao;

public record AdocaoResponseDTO(
        Long id,
        Long animalId,
        String nomeAnimal,
        String mensagem,
        String status) {

}
