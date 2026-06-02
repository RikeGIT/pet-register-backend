package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.auth.AuthResponseDTO;
import com.ducktecnology.pet_register.dto.LoginRequestDTO;
import com.ducktecnology.pet_register.dto.auth.MeResponseDTO;
import com.ducktecnology.pet_register.dto.auth.OtpChallengeResponseDTO;
import com.ducktecnology.pet_register.dto.auth.OtpVerificationRequestDTO;
import com.ducktecnology.pet_register.dto.auth.RefreshRequestDTO;
import com.ducktecnology.pet_register.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<OtpChallengeResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO dto
    ) {

        return ResponseEntity.ok(service.login(dto));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<AuthResponseDTO> verifyOtp(
            @RequestBody @Valid OtpVerificationRequestDTO dto
    ) {
        return ResponseEntity.ok(service.verificarOtp(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @RequestBody RefreshRequestDTO dto
    ) {

        return ResponseEntity.ok(service.refresh(dto));
    }
    @GetMapping("/me")
    public ResponseEntity<MeResponseDTO> me() {
        return ResponseEntity.ok(service.me());
    }
}
