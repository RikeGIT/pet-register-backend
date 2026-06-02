package com.ducktecnology.pet_register.dto.servico;

public record ServicoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Double preco,
        Integer duracaoMinutos,
        boolean ativo
) {
}
