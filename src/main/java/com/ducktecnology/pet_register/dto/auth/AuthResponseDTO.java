package com.ducktecnology.pet_register.dto.auth;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType
) {
}
