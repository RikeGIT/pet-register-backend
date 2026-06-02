package com.ducktecnology.pet_register.dto.usuario;

import com.ducktecnology.pet_register.domain.enums.Perfil;

public record UsuarioAdminResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        String telefone,
        Perfil perfil,
        Boolean ativo
) {
}