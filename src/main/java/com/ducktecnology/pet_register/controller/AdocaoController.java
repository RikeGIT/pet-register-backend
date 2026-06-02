package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.dto.adocao.AdocaoRequestDTO;
import com.ducktecnology.pet_register.dto.adocao.AdocaoResponseDTO;
import com.ducktecnology.pet_register.service.AdocaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adocoes")
@RequiredArgsConstructor
public class AdocaoController {

    private final AdocaoService service;

    @PostMapping
    public ResponseEntity<AdocaoResponseDTO> criar(@RequestBody AdocaoRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<AdocaoResponseDTO>> listarMinhas() {
        return ResponseEntity.ok(service.listarMinhas());
    }

    @GetMapping
    public ResponseEntity<List<AdocaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    // Endpoint de Moderação (ex: PATCH /api/adocoes/1/status?status=APROVADO)
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusSolicitacao status) {
        service.atualizarStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}