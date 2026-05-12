package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.animal.AnimalRequestDTO;
import com.ducktecnology.pet_register.dto.animal.AnimalResponseDTO;
import com.ducktecnology.pet_register.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/animals")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService service;

    @PostMapping
    public ResponseEntity<AnimalResponseDTO> criar(
            @RequestBody AnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AnimalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> buscar(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody AnimalRequestDTO dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id
    ) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
