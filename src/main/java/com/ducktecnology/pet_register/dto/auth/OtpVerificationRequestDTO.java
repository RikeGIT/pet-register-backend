package com.ducktecnology.pet_register.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpVerificationRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String codigo
) {
}
