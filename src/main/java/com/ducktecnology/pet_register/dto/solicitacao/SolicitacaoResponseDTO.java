package com.ducktecnology.pet_register.dto.solicitacao;

import java.time.LocalDateTime;

public record SolicitacaoResponseDTO(
        Long id,
        String tipo,
        Long animalId,
        String nomeAnimal,
        String descricao,
        String contato,
        String status,
        LocalDateTime dataCriacao
) {}