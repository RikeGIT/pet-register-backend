package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoRequestDTO;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoResponseDTO;
import com.ducktecnology.pet_register.service.SolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService service;

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criar(@RequestBody SolicitacaoRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarMinhas() {
        return ResponseEntity.ok(service.listarMinhas());
    }
}
