package com.ducktecnology.pet_register.dto.auth;

public record OtpChallengeResponseDTO(
        boolean otpRequired,
        String email,
        String message
) {
}
