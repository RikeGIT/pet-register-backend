package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.domain.model.Animal;
import com.ducktecnology.pet_register.dto.animal.AnimalRequestDTO;
import com.ducktecnology.pet_register.dto.animal.AnimalResponseDTO;
import com.ducktecnology.pet_register.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

    @PostMapping("/{id}/foto")
    public ResponseEntity<String> uploadFoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Exemplo simples salvando localmente na pasta 'uploads' na raiz do projeto
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
            String fotoUrl = "http://localhost:8080/uploads/" + fileName;
            service.atualizarFoto(id, fotoUrl);

            return ResponseEntity.ok(fotoUrl);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao fazer upload da imagem", e);
        }
    }
}
