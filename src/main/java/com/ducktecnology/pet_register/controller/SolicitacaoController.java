package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.domain.enums.StatusSolicitacao;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoRequestDTO;
import com.ducktecnology.pet_register.dto.solicitacao.SolicitacaoResponseDTO;
import com.ducktecnology.pet_register.service.SolicitacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping
    public ResponseEntity<List<SolicitacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusSolicitacao status,
            @RequestParam(required = false) String horario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        service.atualizarStatus(id, status, horario, data);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
