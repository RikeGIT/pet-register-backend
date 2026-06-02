package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.servico.ServicoRequestDTO;
import com.ducktecnology.pet_register.dto.servico.ServicoResponseDTO;
import com.ducktecnology.pet_register.dto.servico.UpdateServicoRequestDTO;
import com.ducktecnology.pet_register.service.ServicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService service;

    @GetMapping("/api/servicos")
    public ResponseEntity<List<ServicoResponseDTO>> listarPublicos() {
        return ResponseEntity.ok(service.listarPublicos());
    }

    @GetMapping("/api/admin/servicos")
    public ResponseEntity<List<ServicoResponseDTO>> listarAdmin() {
        return ResponseEntity.ok(service.listarAdmin());
    }

    @PostMapping("/api/admin/servicos")
    public ResponseEntity<ServicoResponseDTO> criar(@RequestBody ServicoRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @PutMapping("/api/admin/servicos/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UpdateServicoRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/api/admin/servicos/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
