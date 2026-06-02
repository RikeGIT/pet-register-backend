package com.ducktecnology.pet_register.dto.servico;

public record UpdateServicoRequestDTO(
        String nome,
        String descricao,
        Double preco,
        Integer duracaoMinutos,
        Boolean ativo
) {
}
