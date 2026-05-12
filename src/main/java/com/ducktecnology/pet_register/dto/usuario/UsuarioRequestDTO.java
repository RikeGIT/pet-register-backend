package com.ducktecnology.pet_register.dto.usuario;

import com.ducktecnology.pet_register.domain.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(

        @NotBlank
        String nome,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String cpf,

        @NotBlank
        String senha,

        Perfil perfil
) {
}