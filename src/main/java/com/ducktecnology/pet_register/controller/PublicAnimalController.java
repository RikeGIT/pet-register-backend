package com.ducktecnology.pet_register.controller;

import com.ducktecnology.pet_register.dto.animal.AnimalResponseDTO;
import com.ducktecnology.pet_register.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicAnimalController {

    private final AnimalRepository animalRepository;

    @GetMapping("/animals")
    public ResponseEntity<Page<AnimalResponseDTO>> listarPublicos(
            @RequestParam(required = false) String especie,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return null;
    }

    @GetMapping("/featured-animals")
    public ResponseEntity<List<AnimalResponseDTO>> listarDestaques() {
        return null;
    }
}
