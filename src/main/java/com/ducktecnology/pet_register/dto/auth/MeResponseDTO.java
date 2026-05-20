package com.ducktecnology.pet_register.dto.auth;

import com.ducktecnology.pet_register.domain.enums.Perfil;

public record MeResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        Perfil perfil
) {
}