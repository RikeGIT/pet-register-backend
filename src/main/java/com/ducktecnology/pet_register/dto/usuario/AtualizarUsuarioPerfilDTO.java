package com.ducktecnology.pet_register.dto.usuario;

import com.ducktecnology.pet_register.domain.enums.Perfil;

public record AtualizarUsuarioPerfilDTO(
        Perfil perfil
) {
}